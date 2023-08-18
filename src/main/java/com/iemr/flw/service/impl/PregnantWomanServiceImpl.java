package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.ANCVisit;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.domain.iemr.PMSMA;
import com.iemr.flw.domain.iemr.PregnantWomanRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.PmsmaDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.ANCVisitRepo;
import com.iemr.flw.repo.iemr.PmsmaRepo;
import com.iemr.flw.repo.iemr.PregnantWomanRegisterRepo;
import com.iemr.flw.service.PregnantWomanService;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private PmsmaRepo pmsmaRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs) {

        try {
            List<PregnantWomanRegister> pwrList = new ArrayList<>();
            pregnantWomanDTOs.forEach(it -> {
                PregnantWomanRegister pwr =
                        pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if (pwr != null) {
                    Long id = pwr.getId();
                    modelMapper.map(it, pwr);
                    pwr.setId(id);
                } else {
                    pwr = new PregnantWomanRegister();
                    modelMapper.map(it, pwr);
                    pwr.setId(null);
                }
                pwrList.add(pwr);
            });
            pregnantWomanRegisterRepo.save(pwrList);

            logger.info(pregnantWomanDTOs.size() + " Pregnant Woman details saved");
            return "no of pwr details saved: " + pregnantWomanDTOs.size();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PregnantWomanRegister> pregnantWomanRegisterList =
                    pregnantWomanRegisterRepo.getPWRWithBen(user, dto.getFromDate(), dto.getToDate());

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
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ANCVisit> ancVisits = ancVisitRepo.getANCForPW(user, dto.getFromDate(), dto.getToDate());
            return ancVisits.stream()
                    .map(anc -> mapper.convertValue(anc, ANCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs) {
        try {
            List<ANCVisit> ancList = new ArrayList<>();
            ancVisitDTOs.forEach(it -> {
                ANCVisit ancVisit =
                        ancVisitRepo.findANCVisitByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if (ancVisit != null) {
                    Long id = ancVisit.getId();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(id);
                } else {
                    ancVisit = new ANCVisit();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(null);
                }
                ancList.add(ancVisit);
            });
            ancVisitRepo.save(ancList);
            logger.info("ANC visit details saved");
            return "no of anc details saved: " + ancVisitDTOs.size();
        } catch (Exception e) {
            logger.info("Saving ANC visit details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PmsmaDTO> getPmsmaRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PMSMA> pmsmaList = pmsmaRepo.getAllPmsmaByAshaId(user, dto.getFromDate(), dto.getToDate());
            return pmsmaList.stream()
                    .map(pmsma -> mapper.convertValue(pmsma, PmsmaDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String savePmsmaRecords(List<PmsmaDTO> pmsmaDTOs) {
        try {
            List<PMSMA> pmsmaList = new ArrayList<>();
            pmsmaDTOs.forEach(it -> {
                PMSMA pmsma =
                        pmsmaRepo.findPMSMAByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());
                if (pmsma != null) {
                    Long id = pmsma.getId();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(id);
                } else {
                    pmsma = new PMSMA();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(null);
                }
                pmsmaList.add(pmsma);
            });
            pmsmaRepo.save(pmsmaList);
            logger.info("PMSMA details saved");
            return "No. of PMSMA records saved: " + pmsmaDTOs.size();
        } catch (Exception e) {
            logger.info("Saving PMSMA details failed with error : " + e.getMessage());
        }
        return null;
    }
}
