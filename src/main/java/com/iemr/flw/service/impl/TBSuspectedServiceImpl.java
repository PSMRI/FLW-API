package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.TBStopVisitService;
import com.iemr.flw.service.TBSuspectedService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TBSuspectedServiceImpl implements TBSuspectedService {

    private final Logger logger = LoggerFactory.getLogger(TBSuspectedServiceImpl.class);
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private TBSuspectedRepo tbSuspectedRepo;
    @Autowired
    private CampConfigService campConfigService;
    @Autowired
    private TBStopVisitService tbStopVisitService;

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        return null;
    }

    @Override
    public String save(TBSuspectedRequestDTO requestDTO) throws Exception {
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        for (TBSuspectedDTO tbSuspectedDTO : requestDTO.getTbSuspectedList()) {
            BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(tbSuspectedDTO.getBenId(), null,
                    requestDTO.getUserId() != null ? requestDTO.getUserId().toString() : null, vanID, parkingPlaceID);

            TBSuspected tbSuspected =
                    tbSuspectedRepo.getByUserIdAndBenIdAndVisitCode(tbSuspectedDTO.getBenId(), requestDTO.getUserId(), visit.getVisitCode());

            if (tbSuspected == null) {
                tbSuspected = new TBSuspected();
                modelMapper.map(tbSuspectedDTO, tbSuspected);
                tbSuspected.setId(null);
            } else {
                Long id = tbSuspected.getId();
                modelMapper.map(tbSuspectedDTO, tbSuspected);
                tbSuspected.setId(id);
            }

            tbSuspected.setUserId(requestDTO.getUserId());
            tbSuspected.setVisitCode(visit.getVisitCode());
            if (tbSuspected.getVanID() == null && vanID != null) { tbSuspected.setVanID(vanID); tbSuspected.setParkingPlaceID(parkingPlaceID); }
            tbSuspected.setProcessed("N");
            tbSuspectedRepo.save(tbSuspected);
            tbSuspectedRepo.updateVanSerialNo(tbSuspected.getId());
        }
        return "no of tb suspected items saved:" + requestDTO.getTbSuspectedList().size();
    }

    @Override
    public String getByUserId(GetBenRequestHandler request) {
        List<TBSuspectedDTO> dtos = new ArrayList<>();
        List<TBSuspected> tbSuspectedList = tbSuspectedRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        for (TBSuspected tbSuspected : tbSuspectedList) {
            TBSuspectedDTO dto = modelMapper.map(tbSuspected, TBSuspectedDTO.class);
            dto.setUpdateDate(tbSuspected.getLastModDate());
            dto.setUpdatedBy(tbSuspected.getModifiedBy());
            dtos.add(dto);
        }
        TBSuspectedRequestDTO tbSuspectedRequestDTO = new TBSuspectedRequestDTO();
        tbSuspectedRequestDTO.setTbSuspectedList(dtos);
        tbSuspectedRequestDTO.setUserId(request.getAshaId());
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(tbSuspectedRequestDTO);
    }

}
