package com.iemr.flw.service.impl;

import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DiagnosticOrderService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Creates the diagnostic orders a TB screening refers the beneficiary for (chest X-ray, sputum
 * TrueNat) on the camp hub itself, from the screening record it receives.
 *
 * The tablet used to place these orders with its own order/push call right after saving the
 * screening. When the tablet could not reach the hub at that moment the order was marked FAILED on
 * the tablet only and never retried, and the registrar who fills the screening has no access to
 * the X-ray/TrueNat screens to retry it. The screening itself always reaches the hub later through
 * normal sync, so the hub creating the order from it removes the dependency on the tablet being
 * connected at save time.
 *
 * Two triggers, both limited to screenings dated today (hub local date) on this camp's van:
 * - right after a screening save commits (single background thread, so a slow or unreachable
 *   device never holds up screening sync);
 * - a periodic sweep for referred screenings that still have no order, covering screenings saved
 *   before this existed and any attempt that died.
 *
 * Duplicates are prevented by DiagnosticOrderService's per-beneficiary lock and dedup, and by
 * skipping beneficiaries that already have an order of that type today or an open one.
 * Only runs where a camp van is configured, so the central server never sends orders to devices.
 */
@Service
public class StopTBReferralOrderService {

    private static final Logger logger = LoggerFactory.getLogger(StopTBReferralOrderService.class);

    static final String ORDER_EVENT = "STOP_TB_REFERRAL";

    @Autowired
    private DiagnosticOrderService diagnosticOrderService;

    @Autowired
    private DiagnosticOrderRepo diagnosticOrderRepo;

    @Autowired
    private TBScreeningRepo tbScreeningRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private CampConfigService campConfigService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "stoptb-referral-orders");
        t.setDaemon(true);
        return t;
    });

    // Set while a sweep's batch is queued or running, so a backlog (e.g. an unreachable device
    // holding each push to its timeout) is not queued again on every tick.
    private final AtomicBoolean sweepRunning = new AtomicBoolean(false);

    /** One saved screening's referral flags. */
    public record Referral(Long beneficiaryRegID, boolean xray, boolean sputum) {
        boolean any() {
            return xray || sputum;
        }
    }

    /**
     * Queues order creation for the given referrals once the caller's transaction commits (or right
     * away when there is none), so the orders are created against committed screening rows.
     */
    public void createAfterCommit(List<Referral> referrals) {
        if (!campConfigService.isCampConfigured()) return;
        List<Referral> pending = referrals.stream().filter(Referral::any).toList();
        if (pending.isEmpty()) return;
        Runnable submit = () -> executor.submit(() -> pending.forEach(this::createOrders));
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    submit.run();
                }
            });
        } else {
            submit.run();
        }
    }

    // Runs on the shared scheduler thread, so it only reads and hands the work to the referral
    // thread: an order push can wait on the device for its full timeout.
    @Scheduled(fixedDelayString = "${diagnostic.referral-sweep.tick-ms:120000}",
            initialDelayString = "${diagnostic.referral-sweep.initial-delay-ms:60000}")
    public void sweep() {
        if (!campConfigService.isCampConfigured()) return;
        if (!sweepRunning.compareAndSet(false, true)) return;
        try {
            List<TBScreening> referred = tbScreeningRepo.findReferredSince(campConfigService.getVanID(), startOfToday());
            List<Referral> referrals = new ArrayList<>();
            for (TBScreening s : referred) {
                referrals.add(new Referral(s.getBenRegID(),
                        Boolean.TRUE.equals(s.getReferredForDigitalChestXray()),
                        Boolean.TRUE.equals(s.getReferredForSputumCollection())));
            }
            if (referrals.isEmpty()) {
                sweepRunning.set(false);
                return;
            }
            logger.debug("Referral sweep: {} referred screening(s) today", referrals.size());
            executor.submit(() -> {
                try {
                    referrals.forEach(this::createOrders);
                } finally {
                    sweepRunning.set(false);
                }
            });
        } catch (RuntimeException e) {
            sweepRunning.set(false);
            logger.error("Referral sweep failed: {}", e.getMessage(), e);
        }
    }

    void createOrders(Referral referral) {
        try {
            if (referral.beneficiaryRegID() == null) return;
            if (referral.xray()) createIfMissing(referral.beneficiaryRegID(), DiagnosticOrderType.XRAY_CHEST);
            if (referral.sputum()) createIfMissing(referral.beneficiaryRegID(), DiagnosticOrderType.MTB);
        } catch (Exception e) {
            logger.error("Referral orders failed for benRegID={}: {}", referral.beneficiaryRegID(), e.getMessage(), e);
        }
    }

    private void createIfMissing(Long beneficiaryRegID, DiagnosticOrderType orderType) {
        if (!screenedToday(beneficiaryRegID)) return;
        BigInteger benId = beneficiaryRepo.getBenIdFromRegID(beneficiaryRegID);
        if (benId == null) {
            logger.warn("Referral order skipped: no beneficiaryID for benRegID={}", beneficiaryRegID);
            return;
        }
        Long beneficiaryId = benId.longValue();
        if (diagnosticOrderRepo.hasOrderTodayOrOpen(beneficiaryId, orderType.name(), startOfToday())) return;

        DiagnosticOrderRequestDto.PatientDto patient = patientFor(beneficiaryRegID);
        if (patient == null) {
            logger.warn("Referral order skipped: no beneficiary details for benRegID={}", beneficiaryRegID);
            return;
        }
        DiagnosticOrderRequestDto request = new DiagnosticOrderRequestDto();
        request.setBeneficiaryId(beneficiaryId);
        request.setOrderType(orderType.name());
        request.setOrderEvent(ORDER_EVENT);
        request.setPatient(patient);
        try {
            diagnosticOrderService.createAndPushOrderAsSystem(request);
            logger.info("Referral order created for beneficiaryId={}, orderType={}", beneficiaryId, orderType);
        } catch (Exception e) {
            logger.error("Referral order failed for beneficiaryId={}, orderType={}: {}",
                    beneficiaryId, orderType, e.getMessage());
        }
    }

    // The latest screening must be today's: an order is for the patient who is at the camp now,
    // not for one screened on an earlier day whose record only reached the hub today.
    private boolean screenedToday(Long beneficiaryRegID) {
        List<TBScreening> latest = tbScreeningRepo.findLatestByBenRegID(beneficiaryRegID,
                org.springframework.data.domain.PageRequest.of(0, 1));
        return !latest.isEmpty() && latest.get(0).getVisitDate() != null
                && !latest.get(0).getVisitDate().before(startOfToday());
    }

    DiagnosticOrderRequestDto.PatientDto patientFor(Long beneficiaryRegID) {
        List<RMNCHMBeneficiarymapping> mappings =
                beneficiaryRepo.findByBenRegIdFromMapping(BigInteger.valueOf(beneficiaryRegID));
        if (mappings.isEmpty() || mappings.get(0).getBenDetailsId() == null) return null;
        RMNCHMBeneficiarydetail detail = beneficiaryRepo.getDetailsById(mappings.get(0).getBenDetailsId());
        if (detail == null) return null;

        DiagnosticOrderRequestDto.PatientDto patient = new DiagnosticOrderRequestDto.PatientDto();
        patient.setFirstName(detail.getFirstName() != null ? detail.getFirstName().trim() : "");
        patient.setLastName(detail.getLastName() != null ? detail.getLastName().trim() : "");
        patient.setDateOfBirth(detail.getDob() != null ? new SimpleDateFormat("yyyy-MM-dd").format(detail.getDob()) : null);
        patient.setSex(sexOf(detail.getGender(), detail.getGenderId()));
        return patient;
    }

    // Same three values the tablet sent (Male / Female / Other).
    static String sexOf(String gender, Integer genderId) {
        if (gender != null) {
            if (gender.trim().equalsIgnoreCase("Male")) return "Male";
            if (gender.trim().equalsIgnoreCase("Female")) return "Female";
        }
        if (genderId != null) {
            if (genderId == 1) return "Male";
            if (genderId == 2) return "Female";
        }
        return "Other";
    }

    static Timestamp startOfToday() {
        return Timestamp.valueOf(LocalDate.now().atStartOfDay());
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}
