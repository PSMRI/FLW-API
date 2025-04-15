package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.MalariaFollowUp;
import com.iemr.flw.dto.iemr.MalariaFollowListUpDTO;
import com.iemr.flw.dto.iemr.MalariaFollowUpDTO;
import com.iemr.flw.repo.iemr.MalariaFollowUpRepository;
import com.iemr.flw.service.MalariaFollowUpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalariaFollowUpServiceImpl implements MalariaFollowUpService {

    @Autowired
    private MalariaFollowUpRepository repository;

    public String saveFollowUp(MalariaFollowUpDTO malariaFollowUpDTO) {
       for(MalariaFollowListUpDTO dto : malariaFollowUpDTO.getMalariaFollowListUp()){
           if (dto.getTreatmentStartDate().after(new Date())) {
               return "Treatment start date cannot be in the future.";
           }

           if (dto.getTreatmentCompletionDate() != null &&
                   dto.getTreatmentCompletionDate().before(dto.getTreatmentStartDate())) {
               return "Completion date cannot be before treatment start date.";
           }

           if (dto.getReferralDate() != null &&
                   dto.getReferralDate().before(dto.getTreatmentStartDate())) {
               return "Referral date must be after treatment start date.";
           }

           if ("Pf".equalsIgnoreCase(dto.getTreatmentGiven()) &&
                   !(dto.getPfDay1() || dto.getPvDay2() || dto.getPfDay3())) {
               return "At least one Pf day must be selected.";
           }

           if ("Pv".equalsIgnoreCase(dto.getTreatmentGiven()) &&
                   !(dto.getPfDay1() || dto.getPvDay2() || dto.getPfDay3()|| dto.getPvDay4())) {
               return "At least one Pv day must be selected.";
           }

           MalariaFollowUp entity = new MalariaFollowUp();
           BeanUtils.copyProperties(dto, entity);
           repository.save(entity);
           return "Follow-up saved successfully";
       }
       return  "Fail";
    }

    public List<MalariaFollowListUpDTO> getByUserId(Integer userId) {
        return repository.findByUserId(userId).stream().map(entity -> {
            MalariaFollowListUpDTO dto = new MalariaFollowListUpDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

}
