package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.iemr.EligibleCoupleRegisterRepo;
import com.iemr.flw.repo.iemr.EligibleCoupleTrackingRepo;
import com.iemr.flw.service.CoupleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoupleServiceImpl implements CoupleService {

    @Autowired
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Autowired
    private EligibleCoupleTrackingRepo eligibleCoupleTrackingRepo;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs) {

        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it ->{
                EligibleCoupleRegister eligibleCoupleRegister =
                        mapper.convertValue(it, EligibleCoupleRegister.class);
                ecrList.add(eligibleCoupleRegister);
            });
            eligibleCoupleRegisterRepo.save(ecrList);
            return "saved successfully";
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public String registerEligibleCoupleTracking(List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs) {

        try {
            List<EligibleCoupleTracking> ectList = new ArrayList<>();
            eligibleCoupleTrackingDTOs.forEach(it ->{
                EligibleCoupleTracking eligibleCoupleTracking =
                        mapper.convertValue(it, EligibleCoupleTracking.class);
                ectList.add(eligibleCoupleTracking);
            });
            eligibleCoupleTrackingRepo.save(ectList);
            return "saved successfully";
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public EligibleCoupleDTO getEligibleCouple(Long benId) {
        try{
            EligibleCoupleRegister eligibleCoupleRegister = eligibleCoupleRegisterRepo.getECRWithBen(benId);
            return mapper.convertValue(eligibleCoupleRegister, EligibleCoupleDTO.class);
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public List<EligibleCoupleTrackingDTO> getEligibleCoupleTracking(Long ecrId) {

        try {
            List<EligibleCoupleTracking> eligibleCoupleTrackingList = eligibleCoupleTrackingRepo.getDetailsForEC(ecrId);
            return eligibleCoupleTrackingList.stream()
                    .map(ect -> mapper.convertValue(ect, EligibleCoupleTrackingDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }
}
