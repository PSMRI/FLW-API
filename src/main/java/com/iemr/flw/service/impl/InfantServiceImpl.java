package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
import com.iemr.flw.service.InfantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfantServiceImpl implements InfantService {

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private InfantRegisterRepo infantRegisterRepo;

    @Override
    public String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs) {

        try {
            List<InfantRegister> infantList = new ArrayList<>();
            infantRegisterDTOs.forEach(it -> {
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
    public List<InfantRegisterDTO> getInfantDetails(GetBenRequestHandler dto) {
        try {
            List<InfantRegister> infantRegisterList =
                    infantRegisterRepo.getInfantDetailsForUser(dto.getAshaId(), dto.getFromDate(), dto.getToDate());

            return infantRegisterList.stream()
                    .map(infantRegister -> mapper.convertValue(infantRegister, InfantRegisterDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }
}
