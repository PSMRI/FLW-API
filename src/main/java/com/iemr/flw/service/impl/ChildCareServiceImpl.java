package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.CDR;
import com.iemr.flw.domain.iemr.HBYC;
import com.iemr.flw.domain.iemr.MDSR;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.HbycDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.HbycRepo;
import com.iemr.flw.repo.iemr.MdsrRepo;
import com.iemr.flw.service.ChildCareService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildCareServiceImpl implements ChildCareService {

    @Autowired
    private HbycRepo hbycRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerHBYC(List<HbycDTO> hbycDTOs) {
        try {
            List<HBYC> hbycList = new ArrayList<>();
            hbycDTOs.forEach(it ->{
                HBYC hbyc =
                        hbycRepo.findHBYCByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if(hbyc != null) {
                    Long id = hbyc.getId();
                    modelMapper.map(it, hbyc);
                    hbyc.setId(id);
                } else {
                    hbyc = new HBYC();
                    modelMapper.map(it, hbyc);
                    hbyc.setId(null);
                }
                hbycList.add(hbyc);
            });
            hbycRepo.save(hbycList);
            return "no of hbyc details saved: " + hbycDTOs.size();
        } catch (Exception e) {
            return "error while saving hbyc details: " + e.getMessage();
        }
    }

    @Override
    public List<HbycDTO> getHbycRecords(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<HBYC> hbycList =
                    hbycRepo.getAllHbycByBenId(user, dto.getFromDate(), dto.getToDate());
            return hbycList.stream()
                    .map(hbyc -> mapper.convertValue(hbyc, HbycDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }
}
