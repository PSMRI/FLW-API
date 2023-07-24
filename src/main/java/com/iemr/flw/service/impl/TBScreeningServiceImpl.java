package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.iemr.TBScreeningDTO;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.TBScreeningService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        requestDTO.getTbScreeningList().forEach(tbScreeningDTO -> {
            tbScreeningRepo.getByBenId(tbScreeningDTO.getBenId());
        });
//        tbScreeningRepo.save(tbScreeningList);
        return null;
    }

    @Override
    public List<TBScreeningDTO> getByUserId(Integer userId) {
        List<TBScreening> tbScreeningList =  tbScreeningRepo.getByUserId(userId);
//        tbScreeningList.ma
        return null;
    }

}
