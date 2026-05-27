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
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.CoupleService;
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


    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs, MultipartFile kitPhoto1, MultipartFile kitPhoto2) {
        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            List<IncentiveActivityRecord> recordList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
//                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());
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
                    if (existingECR.getNumLiveChildren() == 0 && it.getNumLiveChildren() >= 1 && it.getMarriageFirstChildGap() != null && it.getMarriageFirstChildGap() >= 2) {
                        IncentiveActivity activity1 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("FP_DELAY_2Y", GroupName.FAMILY_PLANNING.getDisplayName());
                        createIncentiveRecord(recordList, it, activity1);
                    } else if (existingECR.getNumLiveChildren() == 1 && it.getNumLiveChildren() >= 2 && it.getFirstAndSecondChildGap() != null && it.getFirstAndSecondChildGap() == 3) {
                        IncentiveActivity activity2 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", GroupName.FAMILY_PLANNING.getDisplayName());
                        createIncentiveRecord(recordList, it, activity2);
                    }
                    Long id = existingECR.getId();

                    modelMapper.map(it, existingECR);
                    existingECR.setId(id);
                } else {
                    existingECR = new EligibleCoupleRegister();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(null);
                }
                if (existingECR.getIsKitHandedOver() && (!existingECR.getKitPhoto1().isEmpty() || !existingECR.getKitPhoto2().isEmpty())) {
                    IncentiveActivity handoverKitActivityAM =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.FAMILY_PLANNING.getDisplayName());
                    if (handoverKitActivityAM != null) {
                        createIncentiveRecord(recordList, it, handoverKitActivityAM);

                    }


                    IncentiveActivity handoverKitActivityCH =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.ACTIVITY.getDisplayName());
                    if (handoverKitActivityCH != null) {
                        createIncentiveRecord(recordList, it, handoverKitActivityCH);

                    }
                }
                ecrList.add(existingECR);
            });
            eligibleCoupleRegisterRepo.saveAll(ecrList);
            recordRepo.saveAll(recordList);
            return "no of ecr details saved: " + ecrList.size();
        } catch (Exception e) {
            return "error while saving ecr details: " + e.getMessage();
        }
    }

    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs) {
        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            List<IncentiveActivityRecord> recordList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
//                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());
                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(it.getBenId());

                if (existingECR != null && null != existingECR.getNumLiveChildren()) {
                    if (it.getNumLiveChildren() >= 1 && it.getMarriageFirstChildGap() != null && it.getMarriageFirstChildGap() >= 2) {
                        IncentiveActivity activity1 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", GroupName.FAMILY_PLANNING.getDisplayName())
                        ;

                        IncentiveActivity activityCH =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", GroupName.ACTIVITY.getDisplayName());
                        createIncentiveRecord(recordList, it, activity1);
                        createIncentiveRecord(recordList, it, activityCH);
                    } else if (it.getNumLiveChildren() >= 2 && it.getMarriageFirstChildGap() != null && it.getMarriageFirstChildGap() >= 3) {
                        IncentiveActivity activity2 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", GroupName.FAMILY_PLANNING.getDisplayName());

                        IncentiveActivity activityCH =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", GroupName.ACTIVITY.getDisplayName());
                        createIncentiveRecord(recordList, it, activity2);
                        createIncentiveRecord(recordList, it, activityCH);
                    }
                    Long id = existingECR.getId();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(id);
                } else {
                    existingECR = new EligibleCoupleRegister();
                    modelMapper.map(it, existingECR);
                    existingECR.setId(null);
                }


                if (existingECR.getIsKitHandedOver()) {
                    IncentiveActivity handoverKitActivityAM =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.FAMILY_PLANNING.getDisplayName());
                    if (handoverKitActivityAM != null) {
                        createIncentiveRecord(recordList, it, handoverKitActivityAM);

                    }


                    IncentiveActivity handoverKitActivityCH =
                            incentivesRepo.findIncentiveMasterByNameAndGroup("FP_NP_KIT", GroupName.ACTIVITY.getDisplayName());
                    if (handoverKitActivityCH != null) {
                        createIncentiveRecord(recordList, it, handoverKitActivityCH);

                    }
                }
                ecrList.add(existingECR);
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

    private void checkAndAddAntaraIncentive(List<IncentiveActivityRecord> recordList, EligibleCoupleTracking ect) {
        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());
        logger.info("Antra" + ect.getMethodOfContraception());
        logger.info("Antra" + ect.getAntraDose());
        if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().contains("ANTRA Injection")) {
            // for CG incentive
            IncentiveActivity antaraActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", GroupName.ACTIVITY.getDisplayName());
            if (antaraActivityCH != null) {
                String dose = ect.getAntraDose();

                List<String> validDoses = Arrays.asList("Dose-1", "Dose-2", "Dose-3", "Dose-4");

                boolean isDose = validDoses.stream().anyMatch(dose::contains);

                if (isDose) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivityCH);

                }
            }
            if (ect.getAntraDose().contains("Dose-1")) {
                IncentiveActivity antaraActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", "FAMILY PLANNING");
                if (antaraActivity != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity);
                }
            } else if (ect.getAntraDose().contains("Dose-2")) {
                IncentiveActivity antaraActivity2 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA2", "FAMILY PLANNING");
                if (antaraActivity2 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity2);
                }
            } else if (ect.getAntraDose().contains("Dose-3")) {
                IncentiveActivity antaraActivity3 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA3", "FAMILY PLANNING");
                if (antaraActivity3 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity3);
                }
            } else if (ect.getAntraDose().contains("Dose-4")) {
                IncentiveActivity antaraActivity4 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA4", "FAMILY PLANNING");
                if (antaraActivity4 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity4);
                }


            } else if (ect.getAntraDose().contains("Dose-5")) {
                IncentiveActivity antaraActivity4 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA5", GroupName.FAMILY_PLANNING.getDisplayName());

                IncentiveActivity antaraActivity4CH =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("FP_ANC_MPA1", GroupName.ACTIVITY.getDisplayName());
                if (antaraActivity4CH != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity4CH);
                }

                if (antaraActivity4 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity4);
                }
            }
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("MALE STERILIZATION")) {

            IncentiveActivity maleSterilizationActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", "FAMILY PLANNING");

            IncentiveActivity maleSterilizationActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", GroupName.ACTIVITY.getDisplayName());
            if (maleSterilizationActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, maleSterilizationActivityAM);
            }

            if (maleSterilizationActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, maleSterilizationActivityCH);
            }
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("FEMALE STERILIZATION")) {

            IncentiveActivity femaleSterilizationActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity femaleSterilizationActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.ACTIVITY.getDisplayName());
            if (femaleSterilizationActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, femaleSterilizationActivityAM);
            }

            if (femaleSterilizationActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, femaleSterilizationActivityCH);
            }
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("MiniLap")) {

            IncentiveActivity miniLapActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MINILAP", "FAMILY PLANNING");
            if (miniLapActivity != null) {
                addIncenticeRecord(recordList, ect, userId, miniLapActivity);
            }
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("Condom")) {

            IncentiveActivity comdomActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", "FAMILY PLANNING");
            if (comdomActivity != null) {
                addIncenticeRecord(recordList, ect, userId, comdomActivity);
            }
        } else if (ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("Copper T (IUCD)")) {

            IncentiveActivity copperTActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", "FAMILY PLANNING");
            if (copperTActivity != null) {
                addIncenticeRecord(recordList, ect, userId, copperTActivity);
            }
        }
        if (ect.getMethodOfContraception() != null && (ect.getMethodOfContraception().contains("POST PARTUM STERILIZATION (PPS WITHIN 7 DAYS OF DELIVERY)") || ect.getMethodOfContraception().contains("MiniLap") || ect.getMethodOfContraception().contains("MALE STERILIZATION") || ect.getMethodOfContraception().contains("FEMALE STERILIZATION"))) {
            IncentiveActivity limitiing2ChildActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_LIMIT_2CHILD", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity limitiing2ChildActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_LIMIT_2CHILD", GroupName.ACTIVITY.getDisplayName());
            if (limitiing2ChildActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, limitiing2ChildActivityAM);
            }

            if (limitiing2ChildActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, limitiing2ChildActivityCH);
            }
        }
    }

    private void addIncenticeRecord(List<IncentiveActivityRecord> recordList, EligibleCoupleTracking ect, Integer userId, IncentiveActivity antaraActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(antaraActivity.getId(), ect.getCreatedDate(), ect.getBenId());
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
            recordList.add(record);
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
