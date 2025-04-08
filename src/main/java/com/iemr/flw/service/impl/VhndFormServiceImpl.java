package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.VillageFormEntry;
import com.iemr.flw.dto.iemr.VhndFormDto;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.VillageFormRepository;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.VhndFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class VhndFormServiceImpl implements VhndFormService {

    @Autowired
    private VillageFormRepository repository;
    @Autowired
    private IncentivesRepo incentivesRepo;
    @Autowired
    private IncentiveService incentiveService;

    public ResponseEntity<?> submitForm(VhndFormDto dto) {
        if (dto.getAshaId() == null || dto.getDate() == null || dto.getPlace() == null || dto.getParticipantCount() <= 0) {
            return ResponseEntity.badRequest().body("Missing or invalid fields");
        }

        IncentiveActivity existing = incentivesRepo.findIncentiveMasterByNameAndGroup("VHND", "ASHA");


        VillageFormEntry entry = new VillageFormEntry();
        entry.setAshaId(dto.getAshaId());
        entry.setFormType("VHND");
        entry.setDate(dto.getDate());
        entry.setPlace(dto.getPlace());
        entry.setParticipantCount(dto.getParticipantCount());
        entry.setImageUrls(dto.getImageUrls());
        entry.setIncentiveAmount(200);
        entry.setFmrCode("3.1.1.6.1");
        entry.setSubmittedAt(LocalDateTime.now());

        repository.save(entry);
        // Call IncentiveService to save to master

        if (existing == null) {
            IncentiveActivity activity = new IncentiveActivity();
            activity.setName("VHND");
            activity.setGroup("ASHA");
            activity.setRate(200);
            activity.setFmrCode("3.1.1.6.1");
            activity.setCreatedBy("system");
            activity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            activity.setIsDeleted(false);
            incentivesRepo.save(activity);
        }


        return ResponseEntity.ok().body("Form submitted successfully. Incentive: Rs. 200");
    }
}
