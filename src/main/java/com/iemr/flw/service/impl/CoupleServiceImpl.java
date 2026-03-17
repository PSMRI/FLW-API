package com.iemr.flw.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.CoupleService;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.incentiveLogicRespo.IncentiveLogicForFamilyPlaningService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoupleServiceImpl implements CoupleService {

    ObjectMapper mapper = new ObjectMapper();
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Autowired
    private EligibleCoupleTrackingRepo eligibleCoupleTrackingRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    private final Logger logger = LoggerFactory.getLogger(CoupleServiceImpl.class);

    @Autowired
    private IncentiveLogicForFamilyPlaningService incentiveLogicService;


    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs, MultipartFile kitPhoto1, MultipartFile kitPhoto2) {
        try {

            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            List<IncentiveActivityRecord> recordList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(it.getBenId());
                if (kitPhoto1 != null) {
                    String kitPhoto1base64Image = null;
                    try {
                        kitPhoto1base64Image = Base64.getEncoder().encodeToString(kitPhoto1.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    existingECR.setKitPhoto1(String.valueOf(kitPhoto1base64Image));

                }


                if (kitPhoto2 != null) {
                    String kitPhoto2base64Image = null;
                    try {
                        kitPhoto2base64Image = Base64.getEncoder().encodeToString(kitPhoto2.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    existingECR.setKitPhoto2(String.valueOf(kitPhoto2base64Image));

                }

                if (existingECR != null && null != existingECR.getNumLiveChildren()) {

                    Long id = existingECR.getId();

                    modelMapper.map(it, existingECR);
                    existingECR.setId(id);
                } else {
                    existingECR = new EligibleCoupleRegister();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(null);
                }

                ecrList.add(existingECR);
                processEligibleCoupleIncentives(it,existingECR);
            });
            eligibleCoupleRegisterRepo.saveAll(ecrList);
            recordRepo.saveAll(recordList);
            return "no of ecr details saved: " + ecrList.size();
        } catch (Exception e) {
            return "error while saving ecr details: " + e.getMessage();
        }
    }

    private void createIncentiveRecord(List<IncentiveActivityRecord> recordList, EligibleCoupleDTO eligibleCoupleDTO, IncentiveActivity activity) {
        if (activity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(activity.getId(), eligibleCoupleDTO.getCreatedDate(), eligibleCoupleDTO.getBenId());
            Integer userId = userRepo.getUserIdByName(eligibleCoupleDTO.getCreatedBy());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(activity.getId());
                record.setCreatedDate(eligibleCoupleDTO.getCreatedDate());
                record.setCreatedBy(eligibleCoupleDTO.getCreatedBy());
                record.setStartDate(eligibleCoupleDTO.getCreatedDate());
                record.setEndDate(eligibleCoupleDTO.getCreatedDate());
                record.setUpdatedDate(eligibleCoupleDTO.getCreatedDate());
                record.setUpdatedBy(eligibleCoupleDTO.getCreatedBy());
                record.setBenId(eligibleCoupleDTO.getBenId());
                record.setAshaId(userId);
                record.setAmount(Long.valueOf(activity.getRate()));
                recordList.add(record);
            }
        }
    }

    @Override
    public String registerEligibleCoupleTracking(List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs) {
        try {
            List<EligibleCoupleTracking> ectList = new ArrayList<>();
            List<IncentiveActivityRecord> recordList = new ArrayList<>();
            eligibleCoupleTrackingDTOs.forEach(it -> {
                EligibleCoupleTracking ect =
                        eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(it.getBenId(), it.getVisitDate());

                if (ect != null) {
                    Long id = ect.getId();
                    modelMapper.map(it, ect);
                    ect.setId(id);
                } else {
                    ect = new EligibleCoupleTracking();
                    modelMapper.map(it, ect);
                    ect.setId(null);
                }
                ectList.add(ect);
                checkAndAddAntaraIncentive(recordList, ect);
            });
            eligibleCoupleTrackingRepo.saveAll(ectList);
            recordRepo.saveAll(recordList);
            return "no of ect details saved: " + ectList.size();
        } catch (Exception e) {
            return "error while saving ect details: " + e.getMessage();
        }
    }

    private void processEligibleCoupleIncentives(
            EligibleCoupleDTO it,
            EligibleCoupleRegister existingECR) {
        Integer stateId = userRepo.getStateIdByUserName(existingECR.getCreatedBy());
        Integer userId = userRepo.getUserIdByName(existingECR.getCreatedBy());

        // Marriage → First Child Gap
        if (existingECR.getNumLiveChildren() == 0 && it.getNumLiveChildren() >= 1
                && it.getMarriageFirstChildGap() != null && it.getMarriageFirstChildGap() >= 2) {

            if (stateId.equals(StateCode.AM.getStateCode())) {

                incentiveLogicService.incentiveForMarriageToFirstChildGap(
                                it.getBenId(), it.getCreatedDate(), it.getCreatedDate(), userId);

            }
        }

        // First → Second Child Gap
        else if (existingECR.getNumLiveChildren() == 1 && it.getNumLiveChildren() >= 2
                && it.getFirstAndSecondChildGap() != null && it.getFirstAndSecondChildGap() >= 3) {

            if (stateId.equals(StateCode.AM.getStateCode())) {
                        incentiveLogicService.incentiveForFirstToSecondChildGap(
                                it.getBenId(), it.getCreatedDate(), it.getCreatedDate(), userId);

            }
        }

        // Kit Distribution
        if (existingECR.getIsKitHandedOver()
                && (existingECR.getKitPhoto1() != null || existingECR.getKitPhoto2() != null)) {

            if (stateId.equals(StateCode.AM.getStateCode())
                    || stateId.equals(StateCode.CG.getStateCode())) {

                        incentiveLogicService.incentiveForKitDistribution(
                                it.getBenId(), it.getCreatedDate(), it.getCreatedDate(), userId);

            }
        }
    }

    private void checkAndAddAntaraIncentive(List<IncentiveActivityRecord> recordList, EligibleCoupleTracking ect) {

        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());

        if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().contains("ANTRA Injection")) {

            String dose = ect.getAntraDose();

            IncentiveActivityRecord record = null;

            if (dose.contains("Dose-1")) {
                record = incentiveLogicService.incentiveForAntaraDose1(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);
            } else if (dose.contains("Dose-2")) {
                record = incentiveLogicService.incentiveForAntaraDose2(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);
            } else if (dose.contains("Dose-3")) {
                record = incentiveLogicService.incentiveForAntaraDose3(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);
            } else if (dose.contains("Dose-4")) {
                record = incentiveLogicService.incentiveForAntaraDose4(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);
            } else if (dose.contains("Dose-5")) {
                record = incentiveLogicService.incentiveForAntaraDose5(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);
            }

            if (record != null) recordList.add(record);
        }

        else if ("MALE STERILIZATION".equals(ect.getMethodOfContraception())) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForMaleSterilization(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }

        else if ("FEMALE STERILIZATION".equals(ect.getMethodOfContraception())) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForFemaleSterilization(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }

        else if ("MiniLap".equals(ect.getMethodOfContraception())) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForMiniLap(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }

        else if ("Condom".equals(ect.getMethodOfContraception())) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForCondom(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }

        else if ("Copper T (IUCD)".equals(ect.getMethodOfContraception())) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForCopperT(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }

        // Limiting 2 child
        if (ect.getMethodOfContraception() != null &&
                (ect.getMethodOfContraception().contains("STERILIZATION") || ect.getMethodOfContraception().contains("MiniLap"))) {

            IncentiveActivityRecord record =
                    incentiveLogicService.incentiveForLimitTwoChildren(ect.getBenId(), ect.getVisitDate(), ect.getVisitDate(), userId);

            if (record != null) recordList.add(record);
        }
    }


    @Override
    public String getEligibleCoupleRegRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<EligibleCoupleRegister> eligibleCoupleRegisterList =
                    eligibleCoupleRegisterRepo.getECRegRecords(user, dto.getFromDate(), dto.getToDate());
            List<EligibleCoupleDTO> list = eligibleCoupleRegisterList.stream()
                    .map(eligibleCoupleRegister -> mapper.convertValue(eligibleCoupleRegister, EligibleCoupleDTO.class))
                    .collect(Collectors.toList());
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("MMM dd, yyyy h:mm:ss a").create();
            return gson.toJson(list);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<EligibleCoupleTrackingDTO> getEligibleCoupleTracking(GetBenRequestHandler dto) {

        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());

            List<EligibleCoupleTracking> eligibleCoupleTrackingList =
                    eligibleCoupleTrackingRepo.getECTrackRecords(user, dto.getFromDate(), dto.getToDate());

            return eligibleCoupleTrackingList.stream()
                    .map(ect -> mapper.convertValue(ect, EligibleCoupleTrackingDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


}
