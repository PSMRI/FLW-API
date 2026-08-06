package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.controller.DeathReportsController;
import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import com.iemr.flw.domain.iemr.IFAFormSubmissionData;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IFAFormFieldsDTO;
import com.iemr.flw.dto.iemr.IFAFormSubmissionRequest;
import com.iemr.flw.dto.iemr.IFAFormSubmissionResponse;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IFAFormSubmissionService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(IFAFormSubmissionServiceImpl.class);


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;
    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Override
    public String saveFormData(List<IFAFormSubmissionRequest> requests, Integer userId) {
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
            checkIFAIncentive(entities,userId);
            return "Form data saved successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while saving form data: " + e.getMessage();
        }
    }

    private void checkIFAIncentive(List<IFAFormSubmissionData> entities,Integer userId) {
        try {
            List<IFAFormSubmissionData> records = repository.findByUserId(userId);
            List<EligibleCoupleRegister> eligibleCoupleRegisters = eligibleCoupleRegisterRepo.getECRegRecords(userService.getUserDetail(userId).getUserName());
            int totalEligibleCouples = eligibleCoupleRegisters.size();
            int totalIFASubmissions = records.size();
            if(totalEligibleCouples>0){
                double percentage =
                        (totalIFASubmissions * 100.0) / totalEligibleCouples;
                if(percentage>=70){
                    Integer stateCode = userService.getUserDetail(userId).getStateId();
                    if(stateCode.equals(StateCode.CG.getStateCode())){
                        IncentiveActivity incentiveActivityCG= incentivesRepo.findIncentiveMasterByNameAndGroup("NATIONAL_IRON_PLUS", GroupName.ACTIVITY.getDisplayName());

                        if(incentiveActivityCG!=null){
                            entities.forEach(ifaFormSubmissionData -> {
                                addIFAIncentive(ifaFormSubmissionData,incentiveActivityCG);

                            });
                        }

                    }
                }
            }
        }catch (Exception e){
            logger.error("Error while processing IFA incentive", e);

        }



    }

    private void addIFAIncentive(IFAFormSubmissionData ifaFormSubmissionData, IncentiveActivity incentiveActivityAM) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate ifaVisitDate = LocalDate.parse(ifaFormSubmissionData.getVisitDate(), formatter);

        Timestamp ifaVisitDateTimestamp = Timestamp.valueOf(ifaVisitDate.atStartOfDay());

        IncentiveActivityRecord incentiveActivityRecord = incentiveRecordRepo.findRecordByActivityIdCreatedDateBenId(incentiveActivityAM.getId(),ifaVisitDateTimestamp,ifaFormSubmissionData.getBeneficiaryId(),userServiceRoleRepo.getUserIdByName(ifaFormSubmissionData.getUserName()));
        if(incentiveActivityRecord==null){
            incentiveActivityRecord = new IncentiveActivityRecord();
            incentiveActivityRecord.setActivityId(incentiveActivityAM.getId());
            incentiveActivityRecord.setCreatedDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setStartDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setEndDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setUpdatedDate(ifaVisitDateTimestamp);
            incentiveActivityRecord.setUpdatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setCreatedBy(ifaFormSubmissionData.getUserName());
            incentiveActivityRecord.setBenId(ifaFormSubmissionData.getBeneficiaryId());
            incentiveActivityRecord.setAshaId(userServiceRoleRepo.getUserIdByName(ifaFormSubmissionData.getUserName()));
            incentiveActivityRecord.setAmount(Long.valueOf(incentiveActivityAM.getRate()));
            incentiveActivityRecord.setIsEligible(true);
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
