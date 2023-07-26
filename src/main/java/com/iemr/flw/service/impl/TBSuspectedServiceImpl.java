package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
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

    @Autowired
    private TBSuspectedRepo tbSuspectedRepo;

    private final Logger logger = LoggerFactory.getLogger(TBSuspectedServiceImpl.class);

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        return null;
    }

    @Override
    public String save(TBSuspectedRequestDTO requestDTO) throws Exception {
        requestDTO.getTbSuspectedList().forEach(tbSuspectedDTO ->
        {
            TBSuspected tbSuspected =
                    tbSuspectedRepo.getByUserIdAndBenId(tbSuspectedDTO.getBenId(), requestDTO.getUserId());

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
            tbSuspectedRepo.save(tbSuspected);
        });
        return null;
    }

    @Override
    public TBSuspectedRequestDTO getByUserId(GetBenRequestHandler request) {
        List<TBSuspectedDTO> dtos = new ArrayList<>();
        List<TBSuspected> tbSuspectedList = tbSuspectedRepo.getByUserId(request.getAshaId(), request.getFromDate(), request.getToDate());
        tbSuspectedList.forEach(tbSuspected -> dtos.add(modelMapper.map(tbSuspected, TBSuspectedDTO.class)));
        TBSuspectedRequestDTO tbSuspectedRequestDTO = new TBSuspectedRequestDTO();
        tbSuspectedRequestDTO.setTbSuspectedList(dtos);
        tbSuspectedRequestDTO.setUserId(request.getAshaId());
        return tbSuspectedRequestDTO;
    }

}
