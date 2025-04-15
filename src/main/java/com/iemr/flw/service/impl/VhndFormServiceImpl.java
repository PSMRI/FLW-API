package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.VillageFormEntry;
import com.iemr.flw.dto.iemr.VilageLevelFormDto;
import com.iemr.flw.dto.iemr.VilageLevelFormListDto;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.repo.iemr.VillageFormRepository;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.VhndFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VhndFormServiceImpl implements VhndFormService {

    @Autowired
    private VillageFormRepository repository;
    @Autowired
    private IncentiveRecordRepo recordRepo;
    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;


    public String  submitForm(VilageLevelFormDto vilageLevelFormDto) {
        for(VilageLevelFormListDto dto : vilageLevelFormDto.getVilageLevelFormList()){
            if (dto.getUserId() == null || dto.getDate() == null || dto.getPlace() == null || dto.getParticipantCount() <= 0) {
                return "Missing or invalid fields";
            }


            VillageFormEntry entry = new VillageFormEntry();
            entry.setUserId(dto.getUserId());
            entry.setFormType(dto.getFormType());
            entry.setDate(dto.getDate());
            entry.setPlace(dto.getPlace());
            entry.setParticipantCount(dto.getParticipantCount());
            entry.setImageUrls(dto.getImageUrls());
            entry.setSubmittedAt(LocalDateTime.now());
            entry.setCreatedDate(Date.valueOf(LocalDate.now()));
            entry.setCreatedBy(dto.getCreatedBy());

            repository.save(entry);
            checkAndAddIncentives(entry);
            return "Form submitted successfully";


        }
        return "Fail";


    }

    @Override
    public List<VillageFormEntry> getAll(Integer userId) {
        return repository.findAll().stream().filter(villageFormEntry -> villageFormEntry.getUserId().equals(userId)).collect(Collectors.toList());
    }


    private void checkAndAddIncentives(VillageFormEntry villageFormEntry) {
        IncentiveActivity villageFormEntryActivity;
        villageFormEntryActivity = incentivesRepo.findIncentiveMasterByNameAndGroup(villageFormEntry.getFormType(), "VILLAGELEVEL");

        if (villageFormEntryActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(villageFormEntryActivity.getId(), Timestamp.valueOf(villageFormEntry.getCreatedDate().toString()), villageFormEntry.getBenId().longValue());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(villageFormEntryActivity.getId());
                record.setCreatedDate(Timestamp.valueOf(villageFormEntry.getCreatedDate().toString()));
                record.setCreatedBy(villageFormEntry.getCreatedBy());
                record.setStartDate(Timestamp.valueOf(villageFormEntry.getCreatedDate().toString()));
                record.setEndDate(Timestamp.valueOf(villageFormEntry.getCreatedDate().toString()));
                record.setUpdatedDate(Timestamp.valueOf(villageFormEntry.getCreatedDate().toString()));
                record.setUpdatedBy(villageFormEntry.getCreatedBy());
                record.setBenId(villageFormEntry.getBenId().longValue());
                record.setAshaId(villageFormEntry.getUserId());
                record.setName(villageFormEntryActivity.getName());
                record.setAmount(Long.valueOf(villageFormEntryActivity.getRate()));
                recordRepo.save(record);

            }
        }
    }
}
