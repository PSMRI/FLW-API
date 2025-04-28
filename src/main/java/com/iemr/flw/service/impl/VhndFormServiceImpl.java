package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.VillageFormEntry;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.*;
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

    @Autowired
    private VhndRepo vhndRepo;


    public String submitForm(VilageLevelFormDto vilageLevelFormDto) {
        for (VilageLevelFormListDto dto : vilageLevelFormDto.getVilageLevelFormList()) {
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
            //checkAndAddIncentives(entry);
            return "Form submitted successfully";


        }
        return "Fail";


    }

    @Override
    public String submitForm(VhndDto dto) {
        for (VHNDFormDTO vhndFormDTO : dto.getEntires()) {
            saveVhndFormData(vhndFormDTO);

        }
        return "Fail" ;
    }

    private String saveVhndFormData(VHNDFormDTO vhndFormDTO) {
        VHNDForm vhndForm = new VHNDForm();
        vhndForm.setVhndDate(vhndFormDTO.getVhndDate());
        vhndForm.setImage2(vhndFormDTO.getImage2());
        vhndForm.setImage1(vhndFormDTO.getImage1());
        vhndForm.setPlace(vhndFormDTO.getPlace());
        vhndForm.setNoOfBeneficiariesAttended(vhndFormDTO.getNoOfBeneficiariesAttended());
        vhndForm.setFormType("VHND");
        vhndRepo.save(vhndForm);
        checkAndAddIncentives(vhndForm);
        return "Save Vhnd Form successfully";


    }

    @Override
    public List<VillageFormEntry> getAll(Integer userId) {
        return repository.findAll().stream().filter(villageFormEntry -> villageFormEntry.getUserId().equals(userId)).collect(Collectors.toList());
    }


    private void checkAndAddIncentives(VHNDForm villageFormEntry) {
        IncentiveActivity villageFormEntryActivity;
        villageFormEntryActivity = incentivesRepo.findIncentiveMasterByNameAndGroup(villageFormEntry.getFormType(), "VILLAGELEVEL");

        if (villageFormEntryActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(villageFormEntryActivity.getId(), Timestamp.valueOf(villageFormEntry.getVhndDate().toString()));
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(villageFormEntryActivity.getId());
                record.setCreatedDate(Timestamp.valueOf(villageFormEntry.getVhndDate().toString()));
                record.setCreatedBy(villageFormEntry.getCreatedBy());
                record.setStartDate(Timestamp.valueOf(villageFormEntry.getVhndDate().toString()));
                record.setEndDate(Timestamp.valueOf(villageFormEntry.getVhndDate().toString()));
                record.setUpdatedDate(Timestamp.valueOf(villageFormEntry.getVhndDate().toString()));
                record.setUpdatedBy(villageFormEntry.getCreatedBy());
                record.setAshaId(villageFormEntry.getUserId());
                record.setName(villageFormEntryActivity.getName());
                record.setAmount(Long.valueOf(villageFormEntryActivity.getRate()));
                recordRepo.save(record);

            }
        }
    }
}
