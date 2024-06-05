package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.CoupleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs) {
        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            List<IncentiveActivityRecord> recordList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
//                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());
                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(it.getBenId());

                if (existingECR != null) {
                    if(existingECR.getNumLiveChildren() == 0 && it.getNumLiveChildren() >= 1 && it.getMarriageFirstChildGap() >= 3) {
                        IncentiveActivity activity1 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING");
                        createIncentiveRecord(recordList, it, activity1);
                    } else if (existingECR.getNumLiveChildren() == 1 && it.getNumLiveChildren() >= 2 && it.getMarriageFirstChildGap() >= 2) {
                        IncentiveActivity activity2 =
                                incentivesRepo.findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", "FAMILY PLANNING");
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
                ecrList.add(existingECR);
            });
            eligibleCoupleRegisterRepo.save(ecrList);
            recordRepo.save(recordList);
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
                record.setName(activity.getName());
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
            eligibleCoupleTrackingRepo.save(ectList);
            recordRepo.save(recordList);
            return "no of ect details saved: " + ectList.size();
        } catch (Exception e) {
            return "error while saving ect details: " + e.getMessage();
        }
    }

    private void checkAndAddAntaraIncentive(List<IncentiveActivityRecord> recordList, EligibleCoupleTracking ect) {
        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());
        List<EligibleCoupleTracking> couplesHadAntara = eligibleCoupleTrackingRepo.findCouplesHadAntara(ect.getBenId());
        Integer numAntaraDosage = 0;
        if(couplesHadAntara != null && couplesHadAntara.size() > 0) {
            numAntaraDosage = couplesHadAntara.size();
        }

        if(ect.getMethodOfContraception() != null && ect.getMethodOfContraception().equals("ANTRA Injection")) {
            if (numAntaraDosage == 0) {
                IncentiveActivity antaraActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_1", "FAMILY PLANNING");
                if (antaraActivity != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity);
                }
            } else if (numAntaraDosage == 1) {
                IncentiveActivity antaraActivity2 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_2", "FAMILY PLANNING");
                if (antaraActivity2 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity2);
                }
            } else if (numAntaraDosage == 2) {
                IncentiveActivity antaraActivity3 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_3", "FAMILY PLANNING");
                if (antaraActivity3 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity3);
                }
            } else if (numAntaraDosage == 3) {
                IncentiveActivity antaraActivity4 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_4", "FAMILY PLANNING");
                if (antaraActivity4 != null) {
                    addIncenticeRecord(recordList, ect, userId, antaraActivity4);
                }
            }
        }
    }

    private void addIncenticeRecord(List<IncentiveActivityRecord> recordList, EligibleCoupleTracking ect, Integer userId, IncentiveActivity antaraActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(antaraActivity.getId(), ect.getCreatedDate(), ect.getBenId());
        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(antaraActivity.getId());
            record.setCreatedDate(ect.getCreatedDate());
            record.setCreatedBy(ect.getCreatedBy());
            record.setName(antaraActivity.getName());
            record.setStartDate(ect.getCreatedDate());
            record.setEndDate(ect.getCreatedDate());
            record.setUpdatedDate(ect.getCreatedDate());
            record.setUpdatedBy(ect.getCreatedBy());
            record.setBenId(ect.getBenId());
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(antaraActivity.getRate()));
            recordList.add(record);
        }
    }

    @Override
    public List<EligibleCoupleDTO> getEligibleCoupleRegRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<EligibleCoupleRegister> eligibleCoupleRegisterList =
                    eligibleCoupleRegisterRepo.getECRegRecords(user, dto.getFromDate(), dto.getToDate());
            return eligibleCoupleRegisterList.stream()
                    .map(eligibleCoupleRegister -> mapper.convertValue(eligibleCoupleRegister, EligibleCoupleDTO.class))
                    .collect(Collectors.toList());
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
