package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.TBStopVisitService;
import com.iemr.flw.service.TBSuspectedService;
import com.iemr.flw.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TBSuspectedServiceImpl implements TBSuspectedService {

    private final Logger logger = LoggerFactory.getLogger(TBSuspectedServiceImpl.class);
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private TBSuspectedRepo tbSuspectedRepo;
    @Autowired
    private CampConfigService campConfigService;
    @Autowired
    private TBStopVisitService tbStopVisitService;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    @Autowired
    private UserService userService;

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        return null;
    }

    @Override
    public String save(TBSuspectedRequestDTO requestDTO) throws Exception {
        int requestedSize = requestDTO.getTbSuspectedList() == null ? 0 : requestDTO.getTbSuspectedList().size();
        logger.info("TBSuspectedServiceImpl.save - start. userId={}, itemCount={}", requestDTO.getUserId(), requestedSize);

        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        logger.info("TBSuspectedServiceImpl.save - vanID={}, parkingPlaceID={}", vanID, parkingPlaceID);

        int savedCount = 0;
        int failedCount = 0;
        for (TBSuspectedDTO tbSuspectedDTO : requestDTO.getTbSuspectedList()) {
            try {
                logger.info("TBSuspectedServiceImpl.save - processing benId={}", tbSuspectedDTO.getBenId());
                Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(tbSuspectedDTO.getBenId());
                logger.info("TBSuspectedServiceImpl.save - benId={} resolved to beneficiaryRegID={}",
                        tbSuspectedDTO.getBenId(), beneficiaryRegID);
                if (beneficiaryRegID == null) {
                    logger.warn("TBSuspectedServiceImpl.save - no beneficiaryRegID found for benId={}, skipping",
                            tbSuspectedDTO.getBenId());
                    failedCount++;
                    continue;
                }

                BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null,
                        requestDTO.getUserId() != null ? requestDTO.getUserId().toString() : null, vanID, parkingPlaceID);
                logger.info("TBSuspectedServiceImpl.save - visitCode={} for beneficiaryRegID={}",
                        visit.getVisitCode(), beneficiaryRegID);

                TBSuspected tbSuspected;
                if (requestDTO.getFromStopTB() != null && requestDTO.getFromStopTB()) {
                    // todo later - userId and visitCode based lookup will be reintroduced once orders are mapped with visitCode
                    List<TBSuspected> existingByBenId = tbSuspectedRepo.getByBenId(tbSuspectedDTO.getBenId());
                    tbSuspected = (existingByBenId == null || existingByBenId.isEmpty()) ? null : existingByBenId.get(0);
                } else {
                    tbSuspected = tbSuspectedRepo.getByUserIdAndBenIdAndVisitCode(tbSuspectedDTO.getBenId(), requestDTO.getUserId(), visit.getVisitCode());
                }

                boolean isNew = tbSuspected == null;
                if (isNew) {
                    tbSuspected = new TBSuspected();
                    modelMapper.map(tbSuspectedDTO, tbSuspected);
                    tbSuspected.setId(null);
                } else {
                    Long id = tbSuspected.getId();
                    modelMapper.map(tbSuspectedDTO, tbSuspected);
                    tbSuspected.setId(id);
                }

                tbSuspected.setUserId(requestDTO.getUserId());
                tbSuspected.setVisitCode(visit.getVisitCode());
                // Stop TB / Nikshay reporting — mirrors what StopTBServiceImpl.saveNurseTBScreening()
                // already does for tb_screening. Without this, tb_suspected.benRegID stays null and
                // reports can't join it directly against i_beneficiarymapping.BenRegId like tb_screening
                // does; they have to bridge through m_beneficiaryregidmapping.beneficiaryid instead.
                tbSuspected.setBenRegID(beneficiaryRegID);
                // created_date/created_by — mobile currently sends no separate "created" timestamp
                // (confirmed against STOP-TB-App's TBSuspectedDTO wire format), so visitDate is the
                // closest real proxy we have for local capture time; server time is the last-resort
                // fallback. Gated on isNew so a later re-save (e.g. sputum result added afterward)
                // never overwrites the true creation time.
                if (isNew) {
                    tbSuspected.setCreatedDate(tbSuspected.getVisitDate() != null
                            ? tbSuspected.getVisitDate() : new Timestamp(System.currentTimeMillis()));
                    tbSuspected.setCreatedBy(requestDTO.getUserId() != null ? requestDTO.getUserId().toString() : null);
                }
                if (tbSuspected.getVanID() == null && vanID != null) { tbSuspected.setVanID(vanID); tbSuspected.setParkingPlaceID(parkingPlaceID); }
                tbSuspected.setProcessed("N");
                tbSuspectedRepo.save(tbSuspected);
                tbSuspectedRepo.updateVanSerialNo(tbSuspected.getId());
                logger.info("TBSuspectedServiceImpl.save - saved id={} for benId={}", tbSuspected.getId(), tbSuspectedDTO.getBenId());
                savedCount++;

                // Was tbSuspected.getIsConfirmed() (unboxes Boolean -> boolean) — threw a
                // NullPointerException for any record where isConfirmed was null rather than
                // false, aborting the rest of this item's processing after the row already saved.
                if (Boolean.TRUE.equals(tbSuspected.getIsConfirmed())) {
                    incentiveLogicService.incentiveForTbSuspected(tbSuspected.getBenId(),tbSuspected.getVisitDate(),tbSuspected.getVisitDate(),tbSuspected.getUserId());
                }
            } catch (Exception e) {
                // Per-item isolation so one bad record doesn't abort the rest of the batch —
                // matches the chunk-level isolation the mobile app already does on its side.
                logger.error("TBSuspectedServiceImpl.save - failed for benId={}: {}",
                        tbSuspectedDTO.getBenId(), e.getMessage(), e);
                failedCount++;
            }
        }
        logger.info("TBSuspectedServiceImpl.save - complete. requested={}, saved={}, failed={}",
                requestedSize, savedCount, failedCount);
        return "no of tb suspected items saved:" + savedCount;
    }

    @Override
    public String getByUserId(GetBenRequestHandler request) {
        List<TBSuspectedDTO> dtos = new ArrayList<>();
        List<TBSuspected> tbSuspectedList = request.getProviderServiceMapID() != null
                ? tbSuspectedRepo.getByProviderServiceMapIdAndVillageId(request.getProviderServiceMapID(), request.getVillageID())
                : tbSuspectedRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        // The incentive check needs the user's state; look it up once per user instead of loading the
        // full user detail (role + facility queries) for every confirmed row.
        Map<Integer, Integer> stateByUser = new HashMap<>();
        Set<Integer> usersWithoutDetail = new HashSet<>();
        for (TBSuspected tbSuspected : tbSuspectedList) {
            TBSuspectedDTO dto = toDto(tbSuspected);
            dto.setUpdateDate(tbSuspected.getLastModDate());
            dto.setUpdatedBy(tbSuspected.getModifiedBy());
            dtos.add(dto);

            if (tbSuspected != null && Boolean.TRUE.equals(tbSuspected.getIsConfirmed())) {
                Integer userId = tbSuspected.getUserId();
                if (!stateByUser.containsKey(userId) && !usersWithoutDetail.contains(userId)) {
                    try {
                        stateByUser.put(userId, userService.getUserDetail(userId).getStateId());
                    } catch (Exception e) {
                        logger.error("Check Tb Suspected Incentive Exception: ", e);
                        usersWithoutDetail.add(userId);
                    }
                }
                if (stateByUser.containsKey(userId)) {
                    incentiveLogicService.incentiveForTbSuspected(
                            tbSuspected.getBenId(),
                            tbSuspected.getVisitDate(),
                            tbSuspected.getVisitDate(),
                            userId,
                            stateByUser.get(userId)
                    );
                }
            }
        }
        TBSuspectedRequestDTO tbSuspectedRequestDTO = new TBSuspectedRequestDTO();
        tbSuspectedRequestDTO.setTbSuspectedList(dtos);
        tbSuspectedRequestDTO.setUserId(request.getAshaId());
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(tbSuspectedRequestDTO);
    }

    // Field-by-field copy of the same-named fields ModelMapper copied (updateDate/updatedBy are set by
    // the caller); ModelMapper's reflection per row was most of this list's time at village scale.
    private static TBSuspectedDTO toDto(TBSuspected s) {
        TBSuspectedDTO dto = new TBSuspectedDTO();
        dto.setId(s.getId());
        dto.setBenId(s.getBenId());
        dto.setVisitLabel(s.getVisitLabel());
        dto.setVisitDate(s.getVisitDate());
        dto.setTypeOfTBCase(s.getTypeOfTBCase());
        dto.setReasonForSuspicion(s.getReasonForSuspicion());
        dto.setIsSputumCollected(s.getIsSputumCollected());
        dto.setSputumSubmittedAt(s.getSputumSubmittedAt());
        dto.setNikshayId(s.getNikshayId());
        dto.setSputumTestResult(s.getSputumTestResult());
        dto.setIsChestXRayDone(s.getIsChestXRayDone());
        dto.setChestXRayResult(s.getChestXRayResult());
        dto.setReasonForRefusalXray(s.getReasonForRefusalXray());
        dto.setReasonForRefusalMTB(s.getReasonForRefusalMTB());
        dto.setReasonForRefusalMDRRIF(s.getReasonForRefusalMDRRIF());
        dto.setReasonForRefusalSputum(s.getReasonForRefusalSputum());
        dto.setMdrRifResult(s.getMdrRifResult());
        dto.setReferralFacility(s.getReferralFacility());
        dto.setIsTBConfirmed(s.getIsTBConfirmed());
        dto.setIsConfirmed(s.getIsConfirmed());
        dto.setIsDRTBConfirmed(s.getIsDRTBConfirmed());
        dto.setReferred(s.getReferred());
        dto.setFollowUps(s.getFollowUps());
        return dto;
    }

}
