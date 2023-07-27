package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.ANCVisit;
import com.iemr.flw.domain.iemr.PregnantWomanRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import com.iemr.flw.repo.iemr.ANCVisitRepo;
import com.iemr.flw.repo.iemr.PregnantWomanRegisterRepo;
import com.iemr.flw.service.PregnantWomanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PregnantWomanServiceImpl implements PregnantWomanService {

    private final Logger logger = LoggerFactory.getLogger(PregnantWomanServiceImpl.class);

    @Autowired
    PregnantWomanRegisterRepo pregnantWomanRegisterRepo;

    @Autowired
    private ANCVisitRepo ancVisitRepo;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs) {

        try {
            List<PregnantWomanRegister> pwrList = new ArrayList<>();
            pregnantWomanDTOs.forEach(it -> {
                        PregnantWomanRegister pregnantWomanRegister =
                                mapper.convertValue(it, PregnantWomanRegister.class);
                        pwrList.add(pregnantWomanRegister);
                    });
            pregnantWomanRegisterRepo.save(pwrList);

            logger.info("Pregnant Woman details saved");
            return "saved successfully";
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto) {
        try{
            List<PregnantWomanRegister> pregnantWomanRegisterList =
                    pregnantWomanRegisterRepo.getPWRWithBen(dto.getAshaId(), dto.getFromDate(), dto.getToDate());

            return pregnantWomanRegisterList.stream()
                    .map(pregnantWomanRegister -> mapper.convertValue(pregnantWomanRegister, PregnantWomanDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<ANCVisitDTO> getANCVisits(GetBenRequestHandler dto) {
        try {
            List<ANCVisit> ancVisits = ancVisitRepo.getANCForPW(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
            return ancVisits.stream()
                    .map(anc -> mapper.convertValue(anc, ANCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // log
        }
        return null;
    }

    @Override
    public String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs) {
        try {
            List<ANCVisit> ancList = new ArrayList<>();
            ancVisitDTOs.forEach(it -> {
                ANCVisit ancVisit = mapper.convertValue(it, ANCVisit.class);
                ancList.add(ancVisit);
            });
            ancVisitRepo.save(ancList);
            logger.info("ANC visit details saved");
            return "saved successfully";
        } catch (Exception e) {
            logger.info("Saving ANC visit details failed");
        }
        return null;
    }
}
