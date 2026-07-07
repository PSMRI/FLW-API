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
import com.iemr.flw.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
    private UserService userService;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    private final Logger logger = LoggerFactory.getLogger(CoupleServiceImpl.class);
    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();



    @Override
    @Transactional
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs,
                                         MultipartFile kitPhoto1,
                                         MultipartFile kitPhoto2) {
        try {

            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            if(!eligibleCoupleDTOs.isEmpty()){
                for (EligibleCoupleDTO it : eligibleCoupleDTOs) {
                 Integer userId = userRepo.getUserIdByName(it.getCreatedBy());
                 Integer stateId = userService.getUserDetail(userId).getStateId();
                    EligibleCoupleRegister existingECR =
                            eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(it.getBenId());

                    boolean isNew = false;

                    if (existingECR == null) {
                        existingECR = new EligibleCoupleRegister();
                        isNew = true;
                    }

                    Long id = existingECR.getId();

                    modelMapper.map(it, existingECR);

                    existingECR.setId(id);

                    // Photo 1
                    if (kitPhoto1 != null && !kitPhoto1.isEmpty()) {
                        existingECR.setKitPhoto1(
                                Base64.getEncoder().encodeToString(kitPhoto1.getBytes()));
                    }

                    // Photo 2
                    if (kitPhoto2 != null && !kitPhoto2.isEmpty()) {
                        existingECR.setKitPhoto2(
                                Base64.getEncoder().encodeToString(kitPhoto2.getBytes()));
                    }

                    // Incentive only for new registration
                    if (isNew) {

                        if (it.getMarriageFirstChildGap() != null
                                && it.getMarriageFirstChildGap() >= 2) {

                            IncentiveActivity activity = null;

                            if (stateId.equals(StateCode.AM.getStateCode())) {
                                activity = incentivesRepo.findIncentiveMasterByNameAndGroup(
                                        "FP_DELAY_2Y",
                                        GroupName.FAMILY_PLANNING.getDisplayName());

                            } else if (stateId.equals(StateCode.CG.getStateCode())) {
                                activity = incentivesRepo.findIncentiveMasterByNameAndGroup(
                                        "FP_DELAY_2Y",
                                        GroupName.ACTIVITY.getDisplayName());
                            }

                            if (activity != null) {
                                createIncentiveRecord(existingECR, activity);
                            }
                        }

                        if (it.getFirstAndSecondChildGap() != null
                                && it.getFirstAndSecondChildGap() >= 3) {

                            if(stateId.equals(StateCode.AM.getStateCode())){
                                IncentiveActivity activity =
                                        incentivesRepo.findIncentiveMasterByNameAndGroup(
                                                "1st_2nd_CHILD_GAP",
                                                GroupName.FAMILY_PLANNING.getDisplayName());

                                createIncentiveRecord(existingECR, activity);

                            }
                            if(stateId.equals(StateCode.CG.getStateCode())){
                                IncentiveActivity activity =
                                        incentivesRepo.findIncentiveMasterByNameAndGroup(
                                                "1st_2nd_CHILD_GAP",
                                                GroupName.ACTIVITY.getDisplayName());

                                createIncentiveRecord(existingECR, activity);
                            }
                        }

                    }

                    // Kit Incentive
                    if (Boolean.TRUE.equals(existingECR.getIsKitHandedOver())
                            && ((existingECR.getKitPhoto1() != null && !existingECR.getKitPhoto1().isEmpty())
                            || (existingECR.getKitPhoto2() != null && !existingECR.getKitPhoto2().isEmpty()))) {
                         if(stateId.equals(StateCode.AM.getStateCode())){
                             IncentiveActivity activity =
                                     incentivesRepo.findIncentiveMasterByNameAndGroup(
                                             "FP_NP_KIT",
                                             GroupName.FAMILY_PLANNING.getDisplayName());

                             if (activity != null) {
                                 createIncentiveRecord(existingECR, activity);
                             }
                         }
                        if(stateId.equals(StateCode.CG.getStateCode())){
                            IncentiveActivity activity =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "FP_NP_KIT",
                                            GroupName.ACTIVITY.getDisplayName());

                            if (activity != null) {
                                createIncentiveRecord(existingECR, activity);
                            }
                        }


                    }

                    ecrList.add(existingECR);
                }

                eligibleCoupleRegisterRepo.saveAll(ecrList);

            }


            return "No of ECR details saved: " + ecrList.size();

        } catch (Exception e) {
            logger.error("Error while saving Eligible Couple Registration", e);
            return "Error while saving ECR details: " + e.getMessage();
        }
    }

    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs) {
        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(it.getBenId());
                if(existingECR!=null){
                    Long id = existingECR.getId();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(id);
                }else {
                    existingECR = new EligibleCoupleRegister();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(null);
                }

                ecrList.add(existingECR);

            });

            eligibleCoupleRegisterRepo.saveAll(ecrList);
            if(!ecrList.isEmpty()){
                Integer userId = userRepo.getUserIdByName(ecrList.get(0).getCreatedBy());
                Integer stateId = userService.getUserDetail(userId).getStateId();
                checkIncentiveForChildGap(stateId,ecrList.get(0).getCreatedBy(),ecrList);

            }
            return "no of ecr details saved: " + ecrList.size();
        } catch (Exception e) {
            return "error while saving ecr details: " + e.getMessage();
        }
    }


    private void checkIncentiveForChildGap(Integer stateId, String userName,List<EligibleCoupleRegister> eligibleCoupleRegisters) {

        logger.info("Checking Child Gap Incentive for user: {}, stateId: {}", userName, stateId);


        logger.info("Eligible Couple Records Found: {}", eligibleCoupleRegisters.size());

        if (!eligibleCoupleRegisters.isEmpty()) {

            eligibleCoupleRegisters.forEach(eligibleCoupleRegister -> {

                logger.info(
                        "Processing EligibleCoupleRegister -> BenId: {}, NumChildren: {}, MarriageFirstChildGap: {}, FirstAndSecondChildGap: {}",
                        eligibleCoupleRegister.getBenId(),
                        eligibleCoupleRegister.getNumChildren(),
                        eligibleCoupleRegister.getMarriageFirstChildGap(),
                        eligibleCoupleRegister.getFirstAndSecondChildGap());

                // Marriage -> First Child Gap
                if(eligibleCoupleRegister.getFirstAndSecondChildGap()!=null){
                    if (eligibleCoupleRegister.getFirstAndSecondChildGap()>=2 ) {

                        logger.info("Marriage -> First Child Gap condition matched.");

                        if (stateId.equals(StateCode.AM.getStateCode())) {

                            logger.info("Fetching incentive for Assam.");

                            IncentiveActivity activity1 =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "FP_DELAY_2Y",
                                            GroupName.FAMILY_PLANNING.getDisplayName());

                            logger.info("Incentive Activity: {}", activity1);

                            createIncentiveRecord(eligibleCoupleRegister, activity1);

                            logger.info("Marriage -> First Child Gap incentive created.");
                        }

                        if (stateId.equals(StateCode.CG.getStateCode())) {

                            logger.info("Fetching incentive for Chhattisgarh.");

                            IncentiveActivity activityCH =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "FP_DELAY_2Y",
                                            GroupName.ACTIVITY.getDisplayName());

                            logger.info("Incentive Activity: {}", activityCH);

                            createIncentiveRecord(eligibleCoupleRegister, activityCH);

                            logger.info("Marriage -> First Child Gap incentive created.");
                        }
                    }
                }


                // First -> Second Child Gap
                if(eligibleCoupleRegister.getMarriageFirstChildGap()!=null){
                    if (eligibleCoupleRegister.getMarriageFirstChildGap()>=3) {

                        logger.info("1st -> 2nd Child Gap condition matched.");

                        if (stateId.equals(StateCode.AM.getStateCode())) {

                            logger.info("Fetching incentive for Assam.");

                            IncentiveActivity activity2 =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "1st_2nd_CHILD_GAP",
                                            GroupName.FAMILY_PLANNING.getDisplayName());

                            logger.info("Incentive Activity: {}", activity2);

                            createIncentiveRecord(eligibleCoupleRegister, activity2);

                            logger.info("1st -> 2nd Child Gap incentive created.");
                        }

                        if (stateId.equals(StateCode.CG.getStateCode())) {

                            logger.info("Fetching incentive for Chhattisgarh.");

                            IncentiveActivity activityCH =
                                    incentivesRepo.findIncentiveMasterByNameAndGroup(
                                            "1st_2nd_CHILD_GAP",
                                            GroupName.ACTIVITY.getDisplayName());

                            logger.info("Incentive Activity: {}", activityCH);

                            createIncentiveRecord(eligibleCoupleRegister, activityCH);

                            logger.info("1st -> 2nd Child Gap incentive created.");
                        }
                    }
                }

                if (eligibleCoupleRegister.getIsKitHandedOver()!=null && eligibleCoupleRegister.getIsKitHandedOver()) {
                    IncentiveActivity handoverKitActivityAM =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.FAMILY_PLANNING.getDisplayName());
                    if (handoverKitActivityAM != null) {
                        createIncentiveRecord(eligibleCoupleRegister, handoverKitActivityAM);

                    }


                    IncentiveActivity handoverKitActivityCH =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.ACTIVITY.getDisplayName());
                    if (handoverKitActivityCH != null) {
                        createIncentiveRecord(eligibleCoupleRegister, handoverKitActivityCH);

                    }
                }




            });

        } else {
            logger.info("No Eligible Couple Register records found for user: {}", userName);
        }

        logger.info("Completed Child Gap Incentive check for user: {}", userName);
    }


    private void createIncentiveRecord(EligibleCoupleRegister eligibleCoupleDTO, IncentiveActivity activity) {
        String lockKey = activity.getId() + "_" + eligibleCoupleDTO.getBenId() + "_" + eligibleCoupleDTO.getCreatedDate();

        Object lock = lockMap.computeIfAbsent(lockKey, k -> new Object());
        synchronized (lock){
            if (activity != null) {
                Integer userId = userRepo.getUserIdByName(eligibleCoupleDTO.getCreatedBy());

                IncentiveActivityRecord record = recordRepo
                        .findRecordByActivityIdCreatedDateBenId(activity.getId(), eligibleCoupleDTO.getCreatedDate(), eligibleCoupleDTO.getBenId(),userId);
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
                    recordRepo.save(record);
                }
            }
        }
        lockMap.remove(lockKey, lock);



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
                checkAndAddAntaraIncentive(ect);
            });
            eligibleCoupleTrackingRepo.saveAll(ectList);
            recordRepo.saveAll(recordList);
            return "no of ect details saved: " + ectList.size();
        } catch (Exception e) {
            return "error while saving ect details: " + e.getMessage();
        }
    }

    private void checkAndAddAntaraIncentive(EligibleCoupleTracking ect) {
        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());
        logger.info("Antra" + ect.getMethodOfContraception());
        logger.info("Antra" + ect.getAntraDose());
        Integer stateId = userService.getUserDetail(userId).getStateId();
        if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().contains("ANTRA Injection")) {
            if(stateId.equals(StateCode.CG.getStateCode())){
                // for CG incentive
                IncentiveActivity antaraActivityCH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", GroupName.ACTIVITY.getDisplayName());
                if (antaraActivityCH != null) {
                    String dose = ect.getAntraDose();

                    List<String> validDoses = Arrays.asList("Dose-1", "Dose-2", "Dose-3", "Dose-4");

                    boolean isDose = validDoses.stream().anyMatch(dose::contains);

                    if (isDose) {
                        addIncenticeRecord(ect, userId, antaraActivityCH);

                    }
                }
            }
            if(stateId.equals(StateCode.AM.getStateCode())){
                if (ect.getAntraDose().contains("Dose-1")) {
                    IncentiveActivity antaraActivity =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", "FAMILY PLANNING");
                    if (antaraActivity != null) {
                        addIncenticeRecord(ect, userId, antaraActivity);
                    }
                } else if (ect.getAntraDose().contains("Dose-2")) {
                    IncentiveActivity antaraActivity2 =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA2", "FAMILY PLANNING");
                    if (antaraActivity2 != null) {
                        addIncenticeRecord(ect, userId, antaraActivity2);
                    }
                } else if (ect.getAntraDose().contains("Dose-3")) {
                    IncentiveActivity antaraActivity3 =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA3", "FAMILY PLANNING");
                    if (antaraActivity3 != null) {
                        addIncenticeRecord(ect, userId, antaraActivity3);
                    }
                } else if (ect.getAntraDose().contains("Dose-4")) {
                    IncentiveActivity antaraActivity4 =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA4", "FAMILY PLANNING");
                    if (antaraActivity4 != null) {
                        addIncenticeRecord(ect, userId, antaraActivity4);
                    }


                }else  if (ect.getAntraDose().contains("Dose-5")) {
                    IncentiveActivity antaraActivity4 =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA5", GroupName.FAMILY_PLANNING.getDisplayName());
                    

                    if (antaraActivity4 != null) {
                        addIncenticeRecord(ect, userId, antaraActivity4);
                    }
                }
            }
            if(stateId.equals(StateCode.CG.getStateCode())){
                IncentiveActivity antaraActivity4CH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", GroupName.ACTIVITY.getDisplayName());
                if (antaraActivity4CH != null) {
                    addIncenticeRecord(ect, userId, antaraActivity4CH);
                }
            }
            
             
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("MALE STERILIZATION")) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity maleSterilizationActivityAM =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", "FAMILY PLANNING");
                
                if (maleSterilizationActivityAM != null) {
                    addIncenticeRecord(ect, userId, maleSterilizationActivityAM);
                }
                
            }
            if(stateId.equals(StateCode.CG.getStateCode())){
                
                IncentiveActivity maleSterilizationActivityCH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", GroupName.ACTIVITY.getDisplayName());
                

                if (maleSterilizationActivityCH != null) {
                    addIncenticeRecord(ect, userId, maleSterilizationActivityCH);
                }
            }
            
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("FEMALE STERILIZATION")) {
            if(stateId.equals(StateCode.CG.getStateCode())){
                IncentiveActivity femaleSterilizationActivityCH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.ACTIVITY.getDisplayName());

                if (femaleSterilizationActivityCH != null) {
                    addIncenticeRecord(ect, userId, femaleSterilizationActivityCH);
                }
            }
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity femaleSterilizationActivityAM =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());
                if (femaleSterilizationActivityAM != null) {
                    addIncenticeRecord(ect, userId, femaleSterilizationActivityAM);
                }

            }


                
            
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("MiniLap")) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity miniLapActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MINILAP", "FAMILY PLANNING");
                if (miniLapActivity != null) {
                    addIncenticeRecord(ect, userId, miniLapActivity);
                }
            }
           
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("Condom")) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity comdomActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", "FAMILY PLANNING");
                if (comdomActivity != null) {
                    addIncenticeRecord(ect, userId, comdomActivity);
                }
            }
            
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("Copper T (IUCD)")) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity copperTActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", "FAMILY PLANNING");
                if (copperTActivity != null) {
                    addIncenticeRecord(ect, userId, copperTActivity);
                }
            }
            
        }
        if (ect.getMethodOfContraception() != null && (ect.getMethodOfContraception().contains("POST PARTUM STERILIZATION (PPS WITHIN 7 DAYS OF DELIVERY)") || ect.getMethodOfContraception().contains("MiniLap") || ect.getMethodOfContraception().contains("MALE STERILIZATION") || ect.getMethodOfContraception().contains("FEMALE STERILIZATION"))) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                IncentiveActivity limitiing2ChildActivityAM =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_LIMIT_2CHILD", GroupName.FAMILY_PLANNING.getDisplayName());
                if (limitiing2ChildActivityAM != null) {
                    addIncenticeRecord(ect, userId, limitiing2ChildActivityAM);
                }
            }
            if(stateId.equals(StateCode.CG.getStateCode())){
                IncentiveActivity limitiing2ChildActivityCH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_LIMIT_2CHILD", GroupName.ACTIVITY.getDisplayName());


                if (limitiing2ChildActivityCH != null) {
                    addIncenticeRecord(ect, userId, limitiing2ChildActivityCH);
                }
            }
            
            
        }
    }

    private void addIncenticeRecord(EligibleCoupleTracking ect, Integer userId, IncentiveActivity antaraActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(antaraActivity.getId(), ect.getCreatedDate(), ect.getBenId(),userId);
        // get bene details

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(antaraActivity.getId());
            record.setCreatedDate(ect.getVisitDate());
            record.setCreatedBy(ect.getCreatedBy());
            record.setStartDate(ect.getVisitDate());
            record.setEndDate(ect.getVisitDate());
            record.setUpdatedDate(ect.getVisitDate());
            record.setUpdatedBy(ect.getCreatedBy());
            record.setBenId(ect.getBenId());
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(antaraActivity.getRate()));
            recordRepo.save(record);
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
        List<IncentiveActivityRecord> recordList = new ArrayList<>();

        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());

            List<EligibleCoupleTracking> eligibleCoupleTrackingList =
                    eligibleCoupleTrackingRepo.getECTrackRecords(user, dto.getFromDate(), dto.getToDate());

            recordRepo.saveAll(recordList);

            return eligibleCoupleTrackingList.stream()
                    .map(ect -> mapper.convertValue(ect, EligibleCoupleTrackingDTO.class))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


}
