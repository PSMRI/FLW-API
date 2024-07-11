package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.ChildRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ChildRegisterDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.ChildRegisterRepo;
import com.iemr.flw.service.ChildService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildServiceImpl implements ChildService {

    private final Logger logger = LoggerFactory.getLogger(CbacServiceImpl.class);
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ChildRegisterRepo childRepo;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    public String getByUserId(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ChildRegister> childRegisterList = childRepo.getChildDetailsForUser(user, dto.getFromDate(), dto.getToDate());

//            List<ChildRegisterDTO> result = new ArrayList<>();
//            childRegisterList.forEach(childRegister -> {
//                ChildRegisterDTO childDTO = modelMapper.map(childRegister, ChildRegisterDTO.class);
//                result.add(childDTO);
//            });
            List<ChildRegisterDTO> result = childRegisterList.stream()
                    .map(childRegister -> modelMapper.map(childRegister, ChildRegisterDTO.class))
                    .collect(Collectors.toList());
            return new Gson().toJson(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String save(List<ChildRegisterDTO> childRegisterDTOs) throws Exception {
        List<ChildRegister> listToBeSaved = new ArrayList<>();
        childRegisterDTOs.forEach(childRegisterDTO ->
        {
            ChildRegister childRegister =
                    childRepo.findChildRegisterByBenIdAndCreatedDate(childRegisterDTO.getBenId(), childRegisterDTO.getCreatedDate());

            if (childRegister == null) {
                childRegister = new ChildRegister();
                modelMapper.map(childRegisterDTO, childRegister);
                childRegister.setId(null);
            } else {
                Long id = childRegister.getId();
                modelMapper.map(childRegisterDTO, childRegister);
                childRegister.setId(id);
            }
            listToBeSaved.add(childRegister);
        });
        childRepo.saveAll(listToBeSaved);
        return "no of child details saved: " + childRegisterDTOs.size();
    }
}
