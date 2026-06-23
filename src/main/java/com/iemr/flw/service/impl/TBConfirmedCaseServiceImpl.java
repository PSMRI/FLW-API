package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.domain.iemr.TBConfirmedCaseDTO;
import com.iemr.flw.domain.iemr.TBConfirmedCase;
import com.iemr.flw.domain.iemr.TBStopVisit;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.repo.iemr.FormResponseRepo;
import com.iemr.flw.repo.iemr.TBConfirmedTreatmentRepository;
import com.iemr.flw.seeder.TbCounsellingFormSeeder;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TBConfirmedCaseServiceImpl implements TBConfirmedCaseService {

    private static final Logger logger = LoggerFactory.getLogger(TBConfirmedCaseServiceImpl.class);

    private final TBConfirmedTreatmentRepository repository;
    private final FormResponseRepo formResponseRepo;
    private final DynamicFormRepo dynamicFormRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CampConfigService campConfigService;

    @Autowired
    private TBStopVisitService tbStopVisitService;

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
                    TBStopVisit visit = tbStopVisitService.getOrCreateVisitForToday(dto.getBenId(), null,
                            modifiedBy, vanID, parkingPlaceID);

                    List<TBConfirmedCase> existing = repository.findByBenIdAndVisitCode(dto.getBenId(), visit.getId());
                    boolean isNew = existing.isEmpty();
                    TBConfirmedCase entity = isNew ? new TBConfirmedCase() : existing.get(0);
                    entity.setBenId(dto.getBenId());
                    entity.setVisitCode(visit.getId());
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
                    if (entity.getVanID() == null && vanID != null) { entity.setVanID(vanID); entity.setParkingPlaceID(parkingPlaceID); }
                    entity.setProcessed("N");
                    if(entity!=null){
                        TBConfirmedCase saved = repository.save(entity);
                        if (isNew) repository.updateVanSerialNo(saved.getId());
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
        List<TBConfirmedCaseDTO> dtoList = list.stream().map(this::toDTO).collect(Collectors.toList());

        List<Long> benIds = dtoList.stream().map(TBConfirmedCaseDTO::getBenId).collect(Collectors.toList());
        Set<Long> counselledBenIds = Collections.emptySet();
        if (!benIds.isEmpty()) {
            Optional<DynamicForm> counsellingForm = dynamicFormRepo.findByFormUuid(TbCounsellingFormSeeder.FORM_UUID);
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
}
