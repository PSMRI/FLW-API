package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.iemr.TbReferralFollowUpDTO;
import com.iemr.flw.dto.iemr.TbReferralFollowUpListDTO;
import com.iemr.flw.dto.iemr.TbTptFollowUpDTO;
import com.iemr.flw.dto.iemr.TbTptFollowUpListDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.seeder.TbCounsellingV2FormSeeder;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.TBConfirmedCaseService;
import com.iemr.flw.service.TBStopVisitService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.LocalDateAdapter;
import com.iemr.flw.utils.response.OutputResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TBConfirmedCaseServiceImpl implements TBConfirmedCaseService {

    private static final Logger logger = LoggerFactory.getLogger(TBConfirmedCaseServiceImpl.class);

    private final TBConfirmedTreatmentRepository repository;
    private final FormResponseRepo formResponseRepo;
    private final DynamicFormRepo dynamicFormRepo;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CampConfigService campConfigService;

    @Autowired
    private TBStopVisitService tbStopVisitService;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private TbReferralFollowUpRepo tbReferralFollowUpRepo;

    @Autowired
    private IncentiveLogicImpl incentiveLogic;

    @Autowired
    private TbTptFollowUpRepo tbTptFollowUpRepo;
    public TBConfirmedCaseServiceImpl(TBConfirmedTreatmentRepository repository,
                                      FormResponseRepo formResponseRepo,
                                      DynamicFormRepo dynamicFormRepo) {
        this.repository = repository;
        this.formResponseRepo = formResponseRepo;
        this.dynamicFormRepo = dynamicFormRepo;
    }

    @Override
    public String save(List<TBConfirmedCaseDTO> request, String authorisation) throws Exception {
        OutputResponse response = new OutputResponse();

        try {
            if (request != null) {
                logger.info("Saving TB confirmed case: " + request);
                Integer vanID = campConfigService.getVanID();
                Integer parkingPlaceID = campConfigService.getParkingPlaceID();

                for(TBConfirmedCaseDTO dto:request){
                    String modifiedBy = jwtUtil.extractUsername(authorisation);
                    Long beneficiaryRegID = beneficiaryRepo.getRegIDFromBenId(dto.getBenId());
                    BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, null,
                            modifiedBy, vanID, parkingPlaceID);

                    List<TBConfirmedCase> existing = repository.findByBenIdAndVisitCode(dto.getBenId(), visit.getVisitCode());
                    boolean isNew = existing.isEmpty();
                    TBConfirmedCase entity = isNew ? new TBConfirmedCase() : existing.get(0);
                    entity.setBenId(dto.getBenId());
                    entity.setVisitCode(visit.getVisitCode());
                    entity.setUserId(jwtUtil.extractUserId(authorisation));
                    entity.setModifiedBy(modifiedBy);
                    entity.setRegimenType(dto.getRegimenType());
                    entity.setTreatmentStartDate(dto.getTreatmentStartDate());
                    entity.setExpectedTreatmentCompletionDate(dto.getExpectedTreatmentCompletionDate());
                    entity.setFollowUpDate(dto.getFollowUpDate());
                    entity.setMonthlyFollowUpDone(dto.getMonthlyFollowUpDone());
                    entity.setAdherenceToMedicines(dto.getAdherenceToMedicines());
                    entity.setAnyDiscomfort(dto.getAnyDiscomfort());
                    entity.setTreatmentCompleted(dto.getTreatmentCompleted());
                    entity.setActualTreatmentCompletionDate(dto.getActualTreatmentCompletionDate());
                    entity.setTreatmentOutcome(dto.getTreatmentOutcome());
                    entity.setDateOfDeath(dto.getDateOfDeath());
                    entity.setPlaceOfDeath(dto.getPlaceOfDeath());
                    entity.setReasonForDeath(dto.getReasonForDeath());
                    entity.setReasonForNotCompleting(dto.getReasonForNotCompleting());
                    // Stop TB / Nikshay reporting — same gap as tb_suspected: benRegID is computed
                    // above for the visit lookup but was never persisted onto the entity.
                    entity.setBenRegID(beneficiaryRegID);
                    // created_by — gated on isNew so a later follow-up save doesn't overwrite who
                    // originally created the record; createdAt already self-populates via the
                    // entity's `= LocalDate.now()` field default on `new TBConfirmedCase()`.
                    if (isNew) {
                        entity.setCreatedBy(modifiedBy);
                    }
                    if (entity.getVanID() == null && vanID != null) { entity.setVanID(vanID); entity.setParkingPlaceID(parkingPlaceID); }
                    entity.setProcessed("N");
                    if(entity!=null){
                        TBConfirmedCase saved = repository.save(entity);
                        if (isNew) repository.updateVanSerialNo(saved.getId());
                        checkIncentive(entity);
                    }
                }

                response.setResponse("TB Confirmed case saved successfully");

            } else {
                response.setError(500, "Invalid/NULL request obj");
            }
        } catch (Exception e) {
            logger.error("Error saving TB confirmed case", e);
            response.setError(5000, "Error saving TB confirmed case: " + e.getMessage());
        }

        return response.toString();
    }

    private void  checkIncentive(TBConfirmedCase entity){
        String regimen = entity.getRegimenType();

        boolean isDrTb =
                "Shorter Regimen (9–12 Months)".equalsIgnoreCase(regimen)
                        || "Longer Regimen (18–24 Months)".equalsIgnoreCase(regimen);

        if (Boolean.TRUE.equals(entity.getTreatmentCompleted())
                && entity.getExpectedTreatmentCompletionDate() != null) {


            if (isDrTb) {
                incentiveLogicService.incentiveForTbFollowUpIsDrTb(
                        entity.getBenId(),
                        Timestamp.valueOf(entity.getExpectedTreatmentCompletionDate().atStartOfDay()),
                        Timestamp.valueOf(entity.getExpectedTreatmentCompletionDate().atStartOfDay()),
                        entity.getUserId()
                );
            } else {
                incentiveLogicService.incentiveForTbFollowUp(
                        entity.getBenId(),
                        Timestamp.valueOf(entity.getExpectedTreatmentCompletionDate().atStartOfDay()),
                        Timestamp.valueOf(entity.getExpectedTreatmentCompletionDate().atStartOfDay()),
                        entity.getUserId()
                );

            }

        }
    }

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        OutputResponse response = new OutputResponse();

        try {
            List<TBConfirmedCase> list = repository.findByBenId(benId);

            if (list != null && !list.isEmpty()) {
                List<TBConfirmedCaseDTO> dtoList = list.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());

                response.setResponse(dtoList.toString());
                list.forEach(this::checkIncentive);

            } else {
                response.setError(404, "No record found for benId: " + benId);
            }

        } catch (Exception e) {
            logger.error("Error getting TB confirmed case by benId", e);
            response.setError(5000, "Error getting TB confirmed case: " + e.getMessage());
        }

        return response.toString();
    }

    @Override
    public String getByUserId(String authorisation) throws Exception {
        Integer userId = jwtUtil.extractUserId(authorisation);
        List<TBConfirmedCase> list = repository.findByUserId(userId);
        return buildTbConfirmedCasesResponse(userId, list);
    }

    @Override
    public String getByProviderServiceMapId(Integer providerServiceMapID, Integer villageID) throws Exception {
        List<TBConfirmedCase> list = repository.getByProviderServiceMapIdAndVillageId(providerServiceMapID, villageID);
        return buildTbConfirmedCasesResponse(null, list);
    }

    @Override
    public String saveReferralFollowUp(TbReferralFollowUpDTO requestDTO, String token) {
        OutputResponse response = new OutputResponse();

        try {

            TbReferralFollowUp tbReferralFollowUp = new TbReferralFollowUp();

            if(!token.isEmpty() && token!=null && requestDTO!=null){
                String userName = jwtUtil.extractUsername(token);
                Integer userId = jwtUtil.extractUserId(token);
                tbReferralFollowUp.setBenId(requestDTO.getBenId());
                tbReferralFollowUp.setHouseHoldId(requestDTO.getHouseHoldId());
                tbReferralFollowUp.setSyncedBy(userName);
                tbReferralFollowUp.setUpdatedBy(userName);
                tbReferralFollowUp.setCreatedBy(userName);
                tbReferralFollowUp.setUserId(userId);
                if(requestDTO.getFields().getReferredOnDate()!=null && !requestDTO.getFields().getFollowUpDate().isEmpty()){
                    Timestamp referOnDate = Timestamp.valueOf(requestDTO.getFields().getReferredOnDate());
                    tbReferralFollowUp.setFollowUpDate(referOnDate);

                }

                if(requestDTO.getFields().getFollowUpDate()!=null && !requestDTO.getFields().getFollowUpDate().isEmpty()){
                    Timestamp followUpDate = Timestamp.valueOf(requestDTO.getFields().getFollowUpDate());
                    tbReferralFollowUp.setFollowUpDate(followUpDate);

                }
                tbReferralFollowUp.setFollowUpStatus(requestDTO.getFields().getFollowUpStatus());
            }

            tbReferralFollowUpRepo.save(tbReferralFollowUp);
            response.setResponse("TB Referral follow up saved successfully");



        }catch (Exception e){
            response.setError(500,e.getMessage());
            return response.toString();
        }


        return  response.toString();
    }

    @Override
    public String getReferralFollowUp(String token) {
        OutputResponse response = new OutputResponse();

        try {
            if (token == null || token.trim().isEmpty()) {
                response.setError(400, "Token is required");
                return response.toString();
            }

            Integer userId = jwtUtil.extractUserId(token);

            List<TbReferralFollowUp> referrals =
                    tbReferralFollowUpRepo.findByUserId(userId);

            List<TbReferralFollowUpDTO> referralDTOs = new ArrayList<>();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (TbReferralFollowUp referral : referrals) {

                TbReferralFollowUpDTO dto = new TbReferralFollowUpDTO();
                dto.setBenId(referral.getBenId());
                dto.setUserId(referral.getUserId());
                dto.setHouseHoldId(referral.getHouseHoldId());

                // Initialize nested DTO before setting fields
                TbReferralFollowUpListDTO fields =
                        new TbReferralFollowUpListDTO();

                if (referral.getReferredOnDate() != null) {
                    fields.setReferredOnDate(
                            referral.getReferredOnDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                if (referral.getFollowUpDate() != null) {
                    fields.setFollowUpDate(
                            referral.getFollowUpDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                fields.setFollowUpStatus(referral.getFollowUpStatus());

                dto.setFields(fields);
                referralDTOs.add(dto);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            response.setResponse(
                    objectMapper.writeValueAsString(referralDTOs)
            );

        } catch (Exception e) {
            // Replace with your project's logger if available
            e.printStackTrace();
            response.setError(500, "Failed to fetch referral follow-up: " + e.getMessage());
        }

        return response.toString();
    }

    @Override
    public String saveTptFollowUp(TbTptFollowUpDTO requestDTO, String token) {

        OutputResponse response = new OutputResponse();

        try {

            TbTptFollowUp tbTptFollowUp = new TbTptFollowUp();

            if (token != null && !token.isEmpty() && requestDTO != null) {

                String userName = jwtUtil.extractUsername(token);
                Integer userId = jwtUtil.extractUserId(token);

                tbTptFollowUp.setBenId(requestDTO.getBenId());
                tbTptFollowUp.setUserId(userId);

                tbTptFollowUp.setCreatedBy(userName);
                tbTptFollowUp.setUpdatedBy(userName);

                if (requestDTO.getFields() != null) {

                    TbTptFollowUpListDTO fields = requestDTO.getFields();

                    tbTptFollowUp.setRegimenType(fields.getRegimenType());

                    if (fields.getTreatmentStartDate() != null
                            && !fields.getTreatmentStartDate().isEmpty()) {

                        tbTptFollowUp.setTreatmentStartDate(
                                Timestamp.valueOf(fields.getTreatmentStartDate())
                        );
                    }

                    if (fields.getExpectedTreatmentCompletionDate() != null
                            && !fields.getExpectedTreatmentCompletionDate().isEmpty()) {

                        tbTptFollowUp.setExpectedTreatmentCompletionDate(
                                Timestamp.valueOf(fields.getExpectedTreatmentCompletionDate())
                        );
                    }

                    if (fields.getFollowUpDate() != null
                            && !fields.getFollowUpDate().isEmpty()) {

                        tbTptFollowUp.setFollowUpDate(
                                Timestamp.valueOf(fields.getFollowUpDate())
                        );
                    }

                    tbTptFollowUp.setFollowUpMonth(
                            fields.getMonthlyFollowUp()
                    );

                    tbTptFollowUp.setAdherenceToMedicines(
                            fields.getMedicineAdherence()
                    );

                    tbTptFollowUp.setAnyDiscomfort(
                            convertBollen(fields.getAnyDiscomfort())
                    );

                    tbTptFollowUp.setTreatmentCompleted(
                            convertBollen(fields.getTreatmentCompleted())
                    );

                    if (fields.getActualCompletionDate() != null
                            && !fields.getActualCompletionDate().isEmpty()) {

                        tbTptFollowUp.setActualTreatmentCompletionDate(
                                Timestamp.valueOf(fields.getActualCompletionDate())
                        );
                    }

                    tbTptFollowUp.setTptOutcome(
                            fields.getTptOutcome()
                    );

                    if ("Death".equalsIgnoreCase(fields.getTptOutcome())) {

                        if (fields.getDateOfDeath() != null
                                && !fields.getDateOfDeath().isEmpty()) {

                            tbTptFollowUp.setDateOfDeath(
                                    Timestamp.valueOf(fields.getDateOfDeath())
                            );
                        }

                        tbTptFollowUp.setPlaceOfDeath(
                                fields.getPlaceOfDeath()
                        );

                        tbTptFollowUp.setReasonForDeath(
                                fields.getReasonForDeath()
                        );
                    }
                }

                tbTptFollowUpRepo.save(tbTptFollowUp);

                createTptIncentiveIfEligible(tbTptFollowUp);
            }

            response.setResponse("TPT follow up saved successfully");

        } catch (Exception e) {

            response.setError(500, e.getMessage());
            return response.toString();
        }

        return response.toString();
    }


    private static final Map<String, Integer> MIN_FOLLOW_UPS = Map.of(
            "1HP", 1,
            "3HP", 3,
            "3RH", 3,
            "4R", 4,
            "6H", 6,
            "6LFX", 6
    );


    private void createTptIncentiveIfEligible(TbTptFollowUp saved) {

        // 1. Basic conditions
        if (!Boolean.TRUE.equals(saved.getTreatmentCompleted())
                || saved.getActualTreatmentCompletionDate() == null
                || saved.getRegimenType() == null
                || saved.getTreatmentStartDate() == null
                || saved.getBenId() == null) {
            return;
        }

        // 2. Regimen ka minimum follow-up count
        Integer minRequired = MIN_FOLLOW_UPS.get(saved.getRegimenType().trim().toUpperCase());
        if (minRequired == null) {
            return; // unknown regimen
        }

        // 3. Isi cycle ke saare follow-ups
        List<TbTptFollowUp> cycleRows = tbTptFollowUpRepo
                .findByBenIdAndRegimenTypeAndTreatmentStartDate(
                        saved.getBenId(),
                        saved.getRegimenType(),
                        saved.getTreatmentStartDate());

        long completedMonths = cycleRows.stream()
                .map(TbTptFollowUp::getFollowUpMonth)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .count();

        if (completedMonths < minRequired) {
            return;
        }
        Timestamp completionDate = saved.getActualTreatmentCompletionDate();

        incentiveLogic.incentiveForTbPreventiveFollowUp(
                saved.getBenId(),
                completionDate,
                completionDate,
                saved.getUserId());
    }

    @Override
    public String getTptFollowUp(String token) {

        OutputResponse response = new OutputResponse();

        try {

            if (token == null || token.trim().isEmpty()) {
                response.setError(400, "Token is required");
                return response.toString();
            }

            Integer userId = jwtUtil.extractUserId(token);

            List<TbTptFollowUp> tptFollowUps =
                    tbTptFollowUpRepo.findByUserId(userId);

            List<TbTptFollowUpDTO> tptFollowUpDTOs =
                    new ArrayList<>();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (TbTptFollowUp tptFollowUp : tptFollowUps) {

                TbTptFollowUpDTO dto = new TbTptFollowUpDTO();

                dto.setBenId(tptFollowUp.getBenId());
                dto.setUserId(tptFollowUp.getUserId());

                TbTptFollowUpListDTO fields =
                        new TbTptFollowUpListDTO();

                // Regimen Type
                fields.setRegimenType(
                        tptFollowUp.getRegimenType()
                );

                // Treatment Start Date
                if (tptFollowUp.getTreatmentStartDate() != null) {
                    fields.setTreatmentStartDate(
                            tptFollowUp.getTreatmentStartDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                // Expected Treatment Completion Date
                if (tptFollowUp.getExpectedTreatmentCompletionDate() != null) {
                    fields.setExpectedTreatmentCompletionDate(
                            tptFollowUp.getExpectedTreatmentCompletionDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                // Follow Up Date
                if (tptFollowUp.getFollowUpDate() != null) {
                    fields.setFollowUpDate(
                            tptFollowUp.getFollowUpDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                // Monthly Follow Up
                fields.setMonthlyFollowUp(
                        tptFollowUp.getFollowUpMonth()
                );

                // Medicine Adherence
                fields.setMedicineAdherence(
                        tptFollowUp.getAdherenceToMedicines()
                );

                // Any Discomfort
                fields.setAnyDiscomfort(
                        convert(tptFollowUp.getAnyDiscomfort())
                );

                // Treatment Completed
                fields.setTreatmentCompleted(
                       convert(tptFollowUp.getTreatmentCompleted())
                );

                // Actual Completion Date
                if (tptFollowUp.getActualTreatmentCompletionDate() != null) {
                    fields.setActualCompletionDate(
                            tptFollowUp.getActualTreatmentCompletionDate()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                // TPT Outcome
                fields.setTptOutcome(
                        tptFollowUp.getTptOutcome()
                );

                // Date of Death
                if (tptFollowUp.getDateOfDeath() != null) {
                    fields.setDateOfDeath(
                            tptFollowUp.getDateOfDeath()
                                    .toLocalDateTime()
                                    .format(formatter)
                    );
                }

                // Place of Death
                fields.setPlaceOfDeath(
                        tptFollowUp.getPlaceOfDeath()
                );

                // Reason for Death
                fields.setReasonForDeath(
                        tptFollowUp.getReasonForDeath()
                );

                dto.setFields(fields);

                tptFollowUpDTOs.add(dto);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            response.setResponse(
                    objectMapper.writeValueAsString(tptFollowUpDTOs)
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setError(
                    500,
                    "Failed to fetch TPT follow-up: " + e.getMessage()
            );
        }

        return response.toString();
    }

    private String buildTbConfirmedCasesResponse(Integer userId, List<TBConfirmedCase> list) {
        List<TBConfirmedCaseDTO> dtoList = list.stream().map(this::toDTO).collect(Collectors.toList());

        List<Long> benIds = dtoList.stream().map(TBConfirmedCaseDTO::getBenId).collect(Collectors.toList());
        Set<Long> counselledBenIds = Collections.emptySet();
        if (!benIds.isEmpty()) {
            Optional<DynamicForm> counsellingForm = dynamicFormRepo.findByFormUuid(TbCounsellingV2FormSeeder.FORM_UUID);
            if (counsellingForm.isPresent()) {
                counselledBenIds = new HashSet<>(formResponseRepo.findCounselledBenIds(benIds, counsellingForm.get().getFormId(), "COMPLETE"));
            }
        }
        final Set<Long> counselled = counselledBenIds;
        dtoList.forEach(dto -> dto.setCounselled(counselled.contains(dto.getBenId())));

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("userId", userId);
        response.put("tbConfirmedCases", dtoList);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setDateFormat("MMM dd, yyyy h:mm:ss a")
                .create();
        return gson.toJson(response);
    }

    // Utility: convert entity -> DTO
    private TBConfirmedCaseDTO toDTO(TBConfirmedCase entity) {
        TBConfirmedCaseDTO dto = new TBConfirmedCaseDTO();

        dto.setId(entity.getId());
        dto.setBenId(entity.getBenId());
        dto.setUserId(entity.getUserId());
        dto.setRegimenType(entity.getRegimenType());
        dto.setTreatmentStartDate(entity.getTreatmentStartDate());
        dto.setExpectedTreatmentCompletionDate(entity.getExpectedTreatmentCompletionDate());
        dto.setFollowUpDate(entity.getFollowUpDate());
        dto.setMonthlyFollowUpDone(entity.getMonthlyFollowUpDone());
        dto.setAdherenceToMedicines(entity.getAdherenceToMedicines());
        dto.setAnyDiscomfort(entity.getAnyDiscomfort());
        dto.setTreatmentCompleted(entity.getTreatmentCompleted());
        dto.setActualTreatmentCompletionDate(entity.getActualTreatmentCompletionDate());
        dto.setTreatmentOutcome(entity.getTreatmentOutcome());
        dto.setDateOfDeath(entity.getDateOfDeath());
        dto.setPlaceOfDeath(entity.getPlaceOfDeath());
        dto.setReasonForDeath(entity.getReasonForDeath());
        dto.setReasonForNotCompleting(entity.getReasonForNotCompleting());
        dto.setUpdateDate(entity.getLastModDate());
        dto.setUpdatedBy(entity.getModifiedBy());

        return dto;
    }

    private String convert(Boolean value) {
        if (value == null) return null;
        return value ? "Yes" : "No";
    }

    private Boolean convertBollen(String value) {
        if (value != null && !value.isEmpty()) {
            return value.equalsIgnoreCase("Yes");
        } else {
            return false;
        }
    }
}
