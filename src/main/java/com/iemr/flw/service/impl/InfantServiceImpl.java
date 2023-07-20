package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.EligibleCoupleRegister;
import com.iemr.flw.domain.InfantRegister;
import com.iemr.flw.dto.EligibleCoupleDTO;
import com.iemr.flw.dto.InfantRegisterDTO;
import com.iemr.flw.repo.EligibleCoupleTrackingRepo;
import com.iemr.flw.repo.InfantRegisterRepo;
import com.iemr.flw.service.InfantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfantServiceImpl implements InfantService {

    @Autowired
    private InfantRegisterRepo infantRegisterRepo;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs) {

        try {
            List<InfantRegister> infantList = new ArrayList<>();
            infantRegisterDTOs.forEach(it ->{
                InfantRegister infantRegister =
                        mapper.convertValue(it, InfantRegister.class);
                infantList.add(infantRegister);
            });
            infantRegisterRepo.save(infantList);
            return "saved successfully";
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public InfantRegisterDTO getInfantDetails(Long benId) {
        try{
            InfantRegister infantRegister = infantRegisterRepo.getInfantDetailsWithBen(benId);
            return mapper.convertValue(infantRegister, InfantRegisterDTO.class);
        } catch (Exception e) {
            // log
        }
        return null;
    }
}
