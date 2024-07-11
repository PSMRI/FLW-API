package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.CDR;
import com.iemr.flw.domain.iemr.MDSR;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.CdrRepo;
import com.iemr.flw.repo.iemr.MdsrRepo;
import com.iemr.flw.service.DeathReportsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeathReportsServiceImpl implements DeathReportsService {

    @Autowired
    private CdrRepo cdrRepo;

    @Autowired
    private MdsrRepo mdsrRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerCDR(List<CdrDTO> cdrDTOs) {
        try {
            List<CDR> cdrList = new ArrayList<>();
            cdrDTOs.forEach(it ->{
                CDR existingCDR =
                        cdrRepo.findCDRByBenId(it.getBenId());

                if(existingCDR != null) {
                    Long id = existingCDR.getId();
                    modelMapper.map(it, existingCDR);
                    existingCDR.setId(id);
                } else {
                    existingCDR = new CDR();
                    modelMapper.map(it, existingCDR);
                    existingCDR.setId(null);
                }
                cdrList.add(existingCDR);
            });
            cdrRepo.saveAll(cdrList);
            return "no of cdr details saved: " + cdrDTOs.size();
        } catch (Exception e) {
            return "error while saving cdr details: " + e.getMessage();
        }
    }

    @Override
    public String registerMDSR(List<MdsrDTO> mdsrDTOs) {
        try {
            List<MDSR> mdsrList = new ArrayList<>();
            mdsrDTOs.forEach(it ->{
                MDSR mdsr =
                        mdsrRepo.findMDSRByBenId(it.getBenId());

                if(mdsr != null) {
                    Long id = mdsr.getId();
                    modelMapper.map(it, mdsr);
                    mdsr.setId(id);
                } else {
                    mdsr = new MDSR();
                    modelMapper.map(it, mdsr);
                    mdsr.setId(null);
                }
                mdsrList.add(mdsr);
            });
            mdsrRepo.saveAll(mdsrList);
            return "no of mdsr details saved: " + mdsrDTOs.size();
        } catch (Exception e) {
            return "error while saving mdsr details: " + e.getMessage();
        }
    }

    @Override
    public List<CdrDTO> getCdrRecords(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<CDR> cdrlist =
                    cdrRepo.getAllCdrByBenId(user, dto.getFromDate(), dto.getToDate());
            return cdrlist.stream()
                    .map(cdr -> mapper.convertValue(cdr, CdrDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public List<MdsrDTO> getMdsrRecords(GetBenRequestHandler dto) {

        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());

            List<MDSR> mdsrList =
                    mdsrRepo.getAllMdsrByAshaId(user, dto.getFromDate(), dto.getToDate());
            return mdsrList.stream()
                    .map(mdsr -> mapper.convertValue(mdsr, MdsrDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }
}
