package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.PregnantWomanHighRiskAssess;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HRPregnantAssessRepo;
import com.iemr.flw.repo.iemr.HRPregnantTrackRepo;
import com.iemr.flw.service.HighRiskPregnantService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HRPregnantServiceImpl implements HighRiskPregnantService {

    @Autowired
    private HRPregnantAssessRepo hrPregnantAssessRepo;

    @Autowired
    private HRPregnantTrackRepo hrPregnantTrackRepo;

    private final Logger logger = LoggerFactory.getLogger(HRPregnantServiceImpl.class);

    private ModelMapper modelMapper = new ModelMapper();


    @Override
    public String getAllAssessments(GetBenRequestHandler request) {
        List<HRPregnantAssessDTO> dtos = new ArrayList<>();
        List<PregnantWomanHighRiskAssess> entities = hrPregnantAssessRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        entities.forEach(entry -> dtos.add(modelMapper.map(entry, HRPregnantAssessDTO.class)));
        UserDataDTO<HRPregnantAssessDTO> userDataDTO = new UserDataDTO<>();
        userDataDTO.setUserId(request.getAshaId());
        userDataDTO.setEntries(dtos);
        return (new Gson().toJson(userDataDTO));
    }

    @Override
    public String saveAllAssessment(UserDataDTO requestDTO) {
        requestDTO.getEntries().forEach(dto ->
        {
            PregnantWomanHighRiskAssess assess = hrPregnantAssessRepo
                    .getByUserIdAndBenId(modelMapper.map(dto, HRPregnantAssessDTO.class).getBenId()
                            , requestDTO.getUserId());

            if (assess == null) {
                assess = new PregnantWomanHighRiskAssess();
                modelMapper.map(dto, assess);
                assess.setId(null);
            } else {
                Long id = assess.getId();
                modelMapper.map(dto, assess);
                assess.setId(id);
            }

            assess.setUserId(requestDTO.getUserId());
            hrPregnantAssessRepo.save(assess);
        });
        return "no of tb screening items saved: " + requestDTO.getEntries().size();
    }

    @Override
    public String saveAllTracking(UserDataDTO requestDTO) {
        return null;
    }

    @Override
    public String getAllTracking(GetBenRequestHandler requestDTO) {
        return null;
    }
}
