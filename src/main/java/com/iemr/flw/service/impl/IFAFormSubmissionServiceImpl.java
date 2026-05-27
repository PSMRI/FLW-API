package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.IFAFormSubmissionData;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IFAFormFieldsDTO;
import com.iemr.flw.dto.iemr.IFAFormSubmissionRequest;
import com.iemr.flw.dto.iemr.IFAFormSubmissionResponse;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.iemr.IFAFormSubmissionRepository;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.IFAFormSubmissionService;
import com.iemr.flw.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class IFAFormSubmissionServiceImpl implements IFAFormSubmissionService {
    @Autowired
    private final IFAFormSubmissionRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;
    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Override
    public String saveFormData(List<IFAFormSubmissionRequest> requests) {
        try {
            List<IFAFormSubmissionData> entities = new ArrayList<>();
            for (IFAFormSubmissionRequest req : requests) {
                IFAFormSubmissionData data = IFAFormSubmissionData.builder()
                        .userId(userServiceRoleRepo.getUserIdByName(req.getUserName()))
                        .beneficiaryId(req.getBeneficiaryId())
                        .formId(req.getFormId())
                        .houseHoldId(req.getHouseHoldId())
                        .userName(req.getUserName())
                        .visitDate(req.getVisitDate())
                        .ifaProvided(req.getFields().getIfa_provided())
                        .ifaQuantity(req.getFields().getIfa_quantity().toString())
                        .build();
                entities.add(data);
            }
            repository.saveAll(entities);
            checkIFAIncentive(entities);
            return "Form data saved successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while saving form data: " + e.getMessage();
        }
    }

    private void checkIFAIncentive(List<IFAFormSubmissionData> entities) {
        IncentiveActivity incentiveActivityAM= incentivesRepo.findIncentiveMasterByNameAndGroup("NIPI_CHILDREN", GroupName.CHILD_HEALTH.getDisplayName());

        if(incentiveActivityAM!=null){
            entities.forEach(ifaFormSubmissionData -> {
                addIFAIncentive(ifaFormSubmissionData,incentiveActivityAM);

            });
        }

    }

    private void addIFAIncentive(IFAFormSubmissionData ifaFormSubmissionData, IncentiveActivity incentiveActivityAM) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate ifaVisitDate = LocalDate.parse(ifaFormSubmissionData.getVisitDate(), formatter);

        Timestamp ifaVisitDateTimestamp = Timestamp.valueOf(ifaVisitDate.atStartOfDay());

        IncentiveActivityRecord incentiveActivityRecord = incentiveRecordRepo.findRecordByActivityIdCreatedDateBenId(incentiveActivityAM.getId(),ifaVisitDateTimestamp,ifaFormSubmissionData.getBeneficiaryId());
        if(incentiveActivityRecord==null){
            incentiveActivityRecord = new IncentiveActivityRecord();
            incentiveActivityRecord.setActivityId(ifaFormSubmissionData.getId());
            incentiveActivityRecord.setCreatedDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setStartDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setEndDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setUpdatedDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setUpdatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setCreatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setBenId(ifaFormSubmissionData.getBeneficiaryId());
            incentiveActivityRecord.setAshaId(userServiceRoleRepo.getUserIdByName(ifaFormSubmissionData.getUserName()));
            incentiveActivityRecord.setAmount(Long.valueOf(incentiveActivityAM.getRate()));
            incentiveRecordRepo.save(incentiveActivityRecord);
        }
    }


    @Override
    public List<IFAFormSubmissionResponse> getFormData(GetBenRequestHandler getBenRequestHandler) {
        List<IFAFormSubmissionData> records = repository.findByUserId(getBenRequestHandler.getAshaId());
        List<IFAFormSubmissionResponse> responses = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (IFAFormSubmissionData entity : records) {
            try {
                // Map domain entity to response DTO
                IFAFormFieldsDTO fieldsDTO = IFAFormFieldsDTO.builder()
                        .visit_date(entity.getVisitDate())
                        .ifa_provided(entity.getIfaProvided())
                        .ifa_quantity(Double.parseDouble(entity.getIfaQuantity()))
                        .build();

                responses.add(IFAFormSubmissionResponse.builder()
                        .formId(entity.getFormId())
                        .houseHoldId(entity.getHouseHoldId())
                        .beneficiaryId(entity.getBeneficiaryId())
                        .visitDate(entity.getVisitDate())
                        .createdBy(entity.getUserName())
                        .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(formatter) : null)
                        .fields(fieldsDTO)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responses;
    }


}
