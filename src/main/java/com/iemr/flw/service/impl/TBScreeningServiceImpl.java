package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningDTO;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.TBScreeningService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TBScreeningServiceImpl implements TBScreeningService {

    @Autowired
    private TBScreeningRepo tbScreeningRepo;

    private final Logger logger = LoggerFactory.getLogger(TBScreeningServiceImpl.class);

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        return null;
    }

    @Override
    public String save(TBScreeningRequestDTO requestDTO) throws Exception {
        requestDTO.getTbScreeningList().forEach(tbScreeningDTO ->
        {
            TBScreening tbScreening =
                    tbScreeningRepo.getByUserIdAndBenId(tbScreeningDTO.getBenId(), requestDTO.getUserId());

            if (tbScreening == null) {
                tbScreening = new TBScreening();
                modelMapper.map(tbScreeningDTO, tbScreening);
                tbScreening.setId(null);
            } else {
                Long id = tbScreening.getId();
                modelMapper.map(tbScreeningDTO, tbScreening);
                tbScreening.setId(id);
            }

            tbScreening.setUserId(requestDTO.getUserId());
            tbScreeningRepo.save(tbScreening);
        });
        return "no of tb screening items saved: " + requestDTO.getTbScreeningList().size();
    }

    @Override
    public String getByUserId(GetBenRequestHandler request) {
        List<TBScreeningDTO> dtos = new ArrayList<>();
        List<TBScreening> tbScreeningList = tbScreeningRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        tbScreeningList.forEach(tbScreening -> dtos.add(modelMapper.map(tbScreening, TBScreeningDTO.class)));
        TBScreeningRequestDTO tbScreeningRequestDTO = new TBScreeningRequestDTO();
        tbScreeningRequestDTO.setTbScreeningList(dtos);
        tbScreeningRequestDTO.setUserId(request.getAshaId());
        return (new Gson()).toJson(tbScreeningRequestDTO);
    }

}
