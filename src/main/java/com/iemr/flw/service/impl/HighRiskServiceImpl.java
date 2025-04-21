package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.service.HighRiskService;
import com.iemr.flw.domain.iemr.HighRiskAssess;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HighRiskAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HighRiskAssessRepo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HighRiskServiceImpl implements HighRiskService {

    private final Logger logger = LoggerFactory.getLogger(HighRiskServiceImpl.class);

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    HighRiskAssessRepo assessRepo;

    @Override
    public String getAllAssessments(GetBenRequestHandler request) {
        List<HighRiskAssessDTO> dtos = new ArrayList<>();
        List<HighRiskAssess> entities = assessRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        entities.forEach(entry -> dtos.add(modelMapper.map(entry, HighRiskAssessDTO.class)));
        UserDataDTO<HighRiskAssessDTO> userDataDTO = new UserDataDTO<>();
        userDataDTO.setUserId(request.getAshaId());
        userDataDTO.setEntries(dtos);
        Gson gson = new GsonBuilder()
                .setDateFormat("MMM d, yyyy h:mm:ss a")  // Set the desired date format
                .create();
        return gson.toJson(userDataDTO);
    }

    @Override
    public String saveAllAssessment(UserDataDTO<HighRiskAssessDTO> requestDTO) {
        requestDTO.getEntries().forEach(dto ->
        {
            HighRiskAssess assess = assessRepo
                    .getByUserIdAndBenId(modelMapper.map(dto, HighRiskAssessDTO.class).getBenId()
                            , requestDTO.getUserId());

            if (assess == null) {
                assess = new HighRiskAssess();
                modelMapper.map(dto, assess);
                assess.setId(null);
            } else {
                Long id = assess.getId();
                modelMapper.map(dto, assess);
                assess.setId(id);
            }

            assess.setUserId(requestDTO.getUserId());
            assessRepo.save(assess);
        });
        return "no of high risk assessment items saved: " + requestDTO.getEntries().size();
    }
}
