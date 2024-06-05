package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
import com.iemr.flw.service.InfantService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfantServiceImpl implements InfantService {

    private final Logger logger = LoggerFactory.getLogger(InfantServiceImpl.class);

    @Autowired
    private InfantRegisterRepo infantRegisterRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs) {

        try {
            List<InfantRegister> infantList = new ArrayList<>();
            infantRegisterDTOs.forEach(it -> {
                InfantRegister infantRegister = infantRegisterRepo.findInfantRegisterByBenIdAndBabyIndexAndIsActive(it.getBenId(), it.getBabyIndex(), true);

                if (infantRegister != null) {
                    Long id = infantRegister.getId();
                    modelMapper.map(it, infantRegister);
                    infantRegister.setId(id);
                } else {
                    infantRegister = new InfantRegister();
                    modelMapper.map(it, infantRegister);
                    infantRegister.setId(null);
                }
                infantList.add(infantRegister);
            });
            infantRegisterRepo.save(infantList);
            return "no of infant register details saved: " + infantList.size();
        } catch (Exception e) {
            logger.info("error while saving infant register details: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<InfantRegisterDTO> getInfantDetails(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<InfantRegister> infantRegisterList =
                    infantRegisterRepo.getInfantDetailsForUser(user, dto.getFromDate(), dto.getToDate());

            return infantRegisterList.stream()
                    .map(infantRegister -> mapper.convertValue(infantRegister, InfantRegisterDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("error while fetching infant register details: " + e.getMessage());
        }
        return null;
    }
}
