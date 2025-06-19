/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.service.impl;

import com.iemr.flw.controller.CoupleController;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.VillageLevelFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VillageLevelFormServiceImpl implements VillageLevelFormService {
    private final Logger logger = LoggerFactory.getLogger(VillageLevelFormServiceImpl.class);

    @Autowired
    private IncentiveRecordRepo recordRepo;
    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private PHCReviewFormRepo phcReviewFormRepo;

    @Autowired
    private DewormingFormRepo dewormingFormRepo;

    @Autowired
    private VhndRepo vhndRepo;

    @Autowired
    private VhncFormRepo vhncFormRepo;

    @Autowired
    private AHDFormRepo ahdFormRepo;


    @Override
    public Boolean saveForm(VhndDto dto) {
        if (dto == null || dto.getEntries() == null) {
            return false;
        }
        for (VHNDFormDTO vhndFormDTO : dto.getEntries()) {
            saveVhndFormData(vhndFormDTO, dto.getUserId());

        }
        return true;
    }

    @Override
    public Boolean saveVhncForm(VhncDto dto) {
        if (dto == null || dto.getEntries() == null) {
            return false;
        }
        for (VhncFormDTO vhncFormDTO : dto.getEntries()) {
            saveVhncFormData(vhncFormDTO, dto.getUserId());
        }
        return true;
    }


    @Override
    public Boolean savePhcForm(PhcReviewMeetingDTO dto) {
        if (dto == null || dto.getEntries() == null) {
            return false;
        }
        for (PhcReviewMeetingFormDTO phcReviewMeetingDTO : dto.getEntries()) {
            savePhcForm(phcReviewMeetingDTO, dto.getUserId());
        }
        return true;
    }

    @Override
    public Boolean saveAhdForm(AhdMeetingDto dto) {
        if (dto == null || dto.getEntries() == null) {
            return false;
        }
        for (AhDMeetingFormDTO ahDMeetingFormDTO : dto.getEntries()) {
            saveAhdForm(ahDMeetingFormDTO, dto.getUserId());
        }
        return true;
    }

    @Override
    public Boolean saveDewormingForm(DewormingDto dto) {
        for (DewormingFormDTO dewormingFormDTO : dto.getEntries()) {
            saveDewormingForm(dewormingFormDTO, dto.getUserId());
        }
        return true;
    }

    private Boolean saveVhncFormData(VhncFormDTO vhncFormDTO, Integer userID) {
        VhncForm vhncForm = new VhncForm();
        vhncForm.setUserId(Long.valueOf(userID));
        vhncForm.setVhncDate(vhncFormDTO.getVhncDate());
        vhncForm.setImage2(vhncFormDTO.getImage2());
        vhncForm.setImage1(vhncFormDTO.getImage1());
        vhncForm.setPlace(vhncFormDTO.getPlace());
        vhncForm.setNoOfBeneficiariesAttended(vhncFormDTO.getNoOfBeneficiariesAttended());

        vhncForm.setFormType("VHNC");
        vhncFormRepo.save(vhncForm);
        checkAndAddIncentives(vhncForm.getVhncDate(), Math.toIntExact(vhncForm.getUserId()), vhncForm.getFormType(), vhncForm.getCreatedBy());

        return true;
    }


    private Boolean savePhcForm(PhcReviewMeetingFormDTO dto, Integer userID) {
        PHCReviewForm phcReviewForm = new PHCReviewForm();
        phcReviewForm.setUserId(Long.valueOf(userID));
        phcReviewForm.setPhcReviewDate(dto.getPhcReviewDate());
        phcReviewForm.setPlace(dto.getPlace());
        phcReviewForm.setNoOfBeneficiariesAttended(dto.getNoOfBeneficiariesAttended());
        phcReviewForm.setImage1(dto.getImage1());
        phcReviewForm.setImage2(dto.getImage2());
        phcReviewForm.setFormType("PHC");
        phcReviewFormRepo.save(phcReviewForm);
        checkAndAddIncentives(phcReviewForm.getPhcReviewDate(), Math.toIntExact(phcReviewForm.getUserId()), phcReviewForm.getFormType(), phcReviewForm.getCreatedBy());

        return true;
    }

    private Boolean saveAhdForm(AhDMeetingFormDTO dto, Integer userID) {
        AHDForm ahdForm = new AHDForm();
        ahdForm.setUserId(Long.valueOf(userID));
        ahdForm.setMobilizedForAHD(dto.getMobilizedForAHD());
        ahdForm.setAhdPlace(dto.getAhdPlace());
        ahdForm.setAhdDate(dto.getAhdDate());
        ahdForm.setImage1(dto.getImage1());
        ahdForm.setImage2(dto.getImage2());
        ahdForm.setFormType("AHD");
        ahdFormRepo.save(ahdForm);
        if (Objects.equals(dto.getMobilizedForAHD(), "Yes")) {
            checkAndAddIncentives(ahdForm.getAhdDate(), Math.toIntExact(ahdForm.getUserId()), ahdForm.getFormType(), ahdForm.getCreatedBy());

        }

        return true;
    }

    private Boolean saveDewormingForm(DewormingFormDTO dto, Integer userID) {
        DewormingForm dewormingForm = new DewormingForm();
        dewormingForm.setUserId(Long.valueOf(userID));
        dewormingForm.setDewormingDone(dto.getDewormingDone());
        dewormingForm.setDewormingDate(dto.getDewormingDate());
        dewormingForm.setDewormingLocation(dto.getDewormingLocation());
        dewormingForm.setAgeGroup(dto.getAgeGroup());
        dewormingForm.setImage1(dto.getImage1());
        dewormingForm.setImage2(dto.getImage2());
        dewormingForm.setFormType("Deworming");
        dewormingFormRepo.save(dewormingForm);
        if (Objects.equals(dewormingForm.getDewormingDone(), "Yes")) {
            checkAndAddIncentives(dewormingForm.getDewormingDate(), Math.toIntExact(dewormingForm.getUserId()), dewormingForm.getFormType(), dewormingForm.getCreatedBy());

        }
        return true;
    }


    private Boolean saveVhndFormData(VHNDFormDTO vhndFormDTO, Integer userID) {
        VHNDForm vhndForm = new VHNDForm();
        vhndForm.setUserId(userID);
        vhndForm.setVhndDate(vhndFormDTO.getVhndDate());
        vhndForm.setImage2(vhndFormDTO.getImage2());
        vhndForm.setImage1(vhndFormDTO.getImage1());
        vhndForm.setPlace(vhndFormDTO.getPlace());
        vhndForm.setNoOfBeneficiariesAttended(vhndFormDTO.getNoOfBeneficiariesAttended());
        vhndForm.setFormType("VHND");
        vhndRepo.save(vhndForm);
        checkAndAddIncentives(vhndForm.getVhndDate(), vhndForm.getUserId(), vhndForm.getFormType(), vhndForm.getCreatedBy());
        return true;


    }

    @Override
    public List<? extends Object> getAll(GetVillageLevelRequestHandler getVillageLevelRequestHandler) {
        if (Objects.equals(getVillageLevelRequestHandler.getFormType(), "VHND")) {
            return vhndRepo.findAll().stream()
                    .filter(vhndForm -> Objects.equals(vhndForm.getUserId().toString(), getVillageLevelRequestHandler.getUserId().toString()))
                    .collect(Collectors.toList());

        } else if (Objects.equals(getVillageLevelRequestHandler.getFormType(), "VHNC")) {
            return vhncFormRepo.findAll().stream()
                    .filter(vhncForm -> Objects.equals(vhncForm.getUserId().toString(), getVillageLevelRequestHandler.getUserId().toString()))
                    .collect(Collectors.toList());

        } else if (Objects.equals(getVillageLevelRequestHandler.getFormType(), "PHC")) {
            return phcReviewFormRepo.findAll().stream()
                    .filter(phcReviewForm -> Objects.equals(phcReviewForm.getUserId().toString(), getVillageLevelRequestHandler.getUserId().toString()))
                    .collect(Collectors.toList());

        } else if (Objects.equals(getVillageLevelRequestHandler.getFormType(), "Deworming")) {
            return dewormingFormRepo.findAll().stream()
                    .filter(dewormingForm -> Objects.equals(dewormingForm.getUserId().toString(), getVillageLevelRequestHandler.getUserId().toString()))
                    .collect(Collectors.toList());

        } else if (Objects.equals(getVillageLevelRequestHandler.getFormType(), "AHD")) {
            return ahdFormRepo.findAll().stream()
                    .filter(ahdForm -> Objects.equals(ahdForm.getUserId().toString(), getVillageLevelRequestHandler.getUserId().toString()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList(); // In case no condition matches

    }


    private void checkAndAddIncentives(String date, Integer userID, String formType, String createdBY) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Parse to LocalDate
        LocalDate localDate = LocalDate.parse(date, formatter);

        // Convert to Timestamp at start of day (00:00:00)
        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
        logger.info("timestamp" + timestamp);

        IncentiveActivity villageFormEntryActivity;
        villageFormEntryActivity = incentivesRepo.findIncentiveMasterByNameAndGroup(formType, "VILLAGELEVEL");

        if (villageFormEntryActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(villageFormEntryActivity.getId(), timestamp, null);
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(villageFormEntryActivity.getId());
                record.setCreatedDate(timestamp);
                record.setCreatedBy(createdBY);
                record.setStartDate(timestamp);
                record.setEndDate(timestamp);
                record.setUpdatedDate(timestamp);
                record.setUpdatedBy(createdBY);
                record.setAshaId(userID);
                record.setName(villageFormEntryActivity.getName());
                record.setAmount(Long.valueOf(villageFormEntryActivity.getRate()));
                recordRepo.save(record);

            }
        }
    }


}