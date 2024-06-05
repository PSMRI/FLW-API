package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskAssess;
import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskTrack;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRNonPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HRNonPregnantTrackDTO;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HRNonPregnantAssessRepo;
import com.iemr.flw.repo.iemr.HRNonPregnantTrackRepo;
import com.iemr.flw.service.HighRiskNonPregnantService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HRNonPregnantServiceImpl implements HighRiskNonPregnantService {

    private final Logger logger = LoggerFactory.getLogger(HRNonPregnantServiceImpl.class);
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private HRNonPregnantAssessRepo assessRepo;
    @Autowired
    private HRNonPregnantTrackRepo trackRepo;

    @Override
    public String getAllAssessment(GetBenRequestHandler request) {
        List<HRNonPregnantAssessDTO> dtos = new ArrayList<>();
        List<NonPregnantWomanHighRiskAssess> entities = assessRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        entities.forEach(entry -> dtos.add(modelMapper.map(entry, HRNonPregnantAssessDTO.class)));
        UserDataDTO<HRNonPregnantAssessDTO> userDataDTO = new UserDataDTO<>();
        userDataDTO.setUserId(request.getAshaId());
        userDataDTO.setEntries(dtos);
        return (new Gson().toJson(userDataDTO));
    }

    @Override
    public String saveAllAssessment(UserDataDTO<HRNonPregnantAssessDTO> requestDTO) {
        requestDTO.getEntries().forEach(dto ->
        {
            NonPregnantWomanHighRiskAssess assess = assessRepo
                    .getByUserIdAndBenId(modelMapper.map(dto, HRPregnantAssessDTO.class).getBenId()
                            , requestDTO.getUserId());

            if (assess == null) {
                assess = new NonPregnantWomanHighRiskAssess();
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

    @Override
    public String getAllTracking(GetBenRequestHandler request) {
        List<HRNonPregnantTrackDTO> dtos = new ArrayList<>();
        List<NonPregnantWomanHighRiskTrack> entities = trackRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        entities.forEach(entry -> dtos.add(modelMapper.map(entry, HRNonPregnantTrackDTO.class)));
        UserDataDTO<HRNonPregnantTrackDTO> userDataDTO = new UserDataDTO<>();
        userDataDTO.setUserId(request.getAshaId());
        userDataDTO.setEntries(dtos);
        return (new Gson().toJson(userDataDTO));
    }

    @Override
    public String saveAllTracking(UserDataDTO<HRNonPregnantTrackDTO> requestDTO) {
        requestDTO.getEntries().forEach(dto ->
        {
            NonPregnantWomanHighRiskTrack track = trackRepo
                    .getByUserIdAndBenId(dto.getBenId(), requestDTO.getUserId(), dto.getVisitDate());

            if (track == null) {
                track = new NonPregnantWomanHighRiskTrack();
                modelMapper.map(dto, track);
                track.setId(null);
            } else {
                Long id = track.getId();
                modelMapper.map(dto, track);
                track.setId(id);
            }

            track.setUserId(requestDTO.getUserId());
            trackRepo.save(track);
        });
        return "no of high risk pregnant tracking items saved: " + requestDTO.getEntries().size();
    }
}
