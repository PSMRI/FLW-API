package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.GeneralOpdEntry;
import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.repo.iemr.BeneficiaryRepository;
import com.iemr.flw.service.GeneralOpdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeneralOpdServiceImpl implements GeneralOpdService {

    @Autowired
    private BeneficiaryRepository repository;

    @Override
    public List<GeneralOpdDto> getOpdListForAsha(String ashaId) {
        List<GeneralOpdEntry> beneficiaries = repository.findByAshaIdOrderByVisitDateDesc(ashaId);
        return beneficiaries.stream().map(b -> {
            GeneralOpdDto dto = new GeneralOpdDto();
            dto.setBeneficiaryName(b.getName());
            dto.setAge(b.getAge());
            dto.setGender(b.getGender());
            dto.setRegistrationDate(b.getRegistrationDate());
            dto.setMobileNumber(b.getMobileNumber());
            dto.setBeneficiaryId(b.getBeneficiaryId());
            dto.setVisitDate(b.getVisitDate());
            dto.setReferredTo(b.getReferredTo());
            dto.setFollowUpDate(b.getFollowUpDate());
            dto.setCallButtonEnabled(b.getMobileNumber() != null && !b.getMobileNumber().isEmpty());
            return dto;
        }).collect(Collectors.toList());
    }
}