package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.EligibleCoupleRegisterRepo;
import com.iemr.flw.repo.iemr.EligibleCoupleTrackingRepo;
import com.iemr.flw.service.CoupleService;
import org.modelmapper.ModelMapper;
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
    private BeneficiaryRepo beneficiaryRepo;

    @Override
    public String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs) {
        try {
            List<EligibleCoupleRegister> ecrList = new ArrayList<>();
            eligibleCoupleDTOs.forEach(it -> {
                EligibleCoupleRegister existingECR =
                        eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if (existingECR != null) {
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
            return "no of ecr details saved: " + eligibleCoupleDTOs.size();
        } catch (Exception e) {
            return "error while saving ecr details: " + e.getMessage();
        }
    }

    @Override
    public String registerEligibleCoupleTracking(List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs) {
        try {
            List<EligibleCoupleTracking> ectList = new ArrayList<>();
            eligibleCoupleTrackingDTOs.forEach(it -> {
                EligibleCoupleTracking ect =
                        eligibleCoupleTrackingRepo.findEligibleCoupleTrackingByEcrIdAndCreatedDate(it.getEcrId(), it.getCreatedDate());

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
            });
            eligibleCoupleTrackingRepo.save(ectList);
            return "no of ect details saved: " + eligibleCoupleTrackingDTOs.size();
        } catch (Exception e) {
            return "error while saving ect details: " + e.getMessage();
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
            // log
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
            // log
        }
        return null;
    }
}
