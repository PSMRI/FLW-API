package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import com.iemr.flw.repo.iemr.AdolescentHealthRepo;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.AdolescentHealthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdolescentHealthServiceImpl implements AdolescentHealthService {
    @Autowired
    private AdolescentHealthRepo adolescentHealthRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;
    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    private static final String INCENTIVE_GROUP = "ADOLESCENT_HEALTH";


    @Override
    @Transactional
    public String saveAll(AdolescentHealthDTO adolescentHealthDTO) {
        if (adolescentHealthDTO == null || adolescentHealthDTO.getAdolescentHealths() == null
                || adolescentHealthDTO.getAdolescentHealths().isEmpty()) {
            return "No records to process";
        }

        for (AdolescentHealth adolescentHealth : adolescentHealthDTO.getAdolescentHealths()) {
            // Check if a record with the same benId already exists in the database
            Optional<AdolescentHealth> existingRecord = adolescentHealthRepo.findByBenId(adolescentHealth.getBenId());

            if (existingRecord.isPresent()) {
                // If record exists, update the existing one
                updateAdolescentHealth(existingRecord, adolescentHealth);
            } else {
                // If the record does not exist, create a new one
                adolescentHealthRepo.save(adolescentHealth);
                checkAndAddIncentives(adolescentHealth);
            }
        }

        return "Records updated/added successfully!";
    }


    public List<AdolescentHealth> getAllAdolescentHealth(GetBenRequestHandler getBenRequestHandler) {
        // Fetch all records from the database
        List<AdolescentHealth> adolescentHealths = adolescentHealthRepo.findByUserId(getBenRequestHandler.getUserId());

        // Convert the list of entity objects to DTO objects
        return adolescentHealths.stream()
                .map(this::copyEntity)
                .collect(Collectors.toList());
    }

    private void updateAdolescentHealth(Optional<AdolescentHealth> existingRecord, AdolescentHealth adolescentHealth) {
        AdolescentHealth existingAdolescentHealth = existingRecord.get();

        // Update fields of the existing record with values from the DTO
        existingAdolescentHealth.setUserId(adolescentHealth.getUserId());
        existingAdolescentHealth.setVisitDate(adolescentHealth.getVisitDate());
        existingAdolescentHealth.setHealthStatus(adolescentHealth.getHealthStatus());
        existingAdolescentHealth.setIfaTabletDistribution(adolescentHealth.getIfaTabletDistribution());
        existingAdolescentHealth.setQuantityOfIfaTablets(adolescentHealth.getQuantityOfIfaTablets());
        existingAdolescentHealth.setMenstrualHygieneAwarenessGiven(adolescentHealth.getMenstrualHygieneAwarenessGiven());
        existingAdolescentHealth.setSanitaryNapkinDistributed(adolescentHealth.getSanitaryNapkinDistributed());
        existingAdolescentHealth.setNoOfPacketsDistributed(adolescentHealth.getNoOfPacketsDistributed());
        existingAdolescentHealth.setPlace(adolescentHealth.getPlace());
        existingAdolescentHealth.setReferredToHealthFacility(adolescentHealth.getReferredToHealthFacility());
        existingAdolescentHealth.setCounselingProvided(adolescentHealth.getCounselingProvided());
        existingAdolescentHealth.setCounselingType(adolescentHealth.getCounselingType());
        existingAdolescentHealth.setFollowUpDate(adolescentHealth.getFollowUpDate());
        existingAdolescentHealth.setReferralStatus(adolescentHealth.getReferralStatus());
        existingAdolescentHealth.setCreatedBy(adolescentHealth.getCreatedBy());
        existingAdolescentHealth.setCreatedDate(adolescentHealth.getCreatedDate());

        // Save the updated record back to the database
        adolescentHealthRepo.save(existingAdolescentHealth);
    }

    private AdolescentHealth copyEntity(AdolescentHealth adolescentHealth) {
        AdolescentHealth dto = new AdolescentHealth();
        dto.setId(adolescentHealth.getId());
        dto.setBenId(adolescentHealth.getBenId());
        dto.setUserId(adolescentHealth.getUserId());
        dto.setVisitDate(adolescentHealth.getVisitDate());
        dto.setHealthStatus(adolescentHealth.getHealthStatus());
        dto.setIfaTabletDistribution(adolescentHealth.getIfaTabletDistribution());
        dto.setQuantityOfIfaTablets(adolescentHealth.getQuantityOfIfaTablets());
        dto.setMenstrualHygieneAwarenessGiven(adolescentHealth.getMenstrualHygieneAwarenessGiven());
        dto.setSanitaryNapkinDistributed(adolescentHealth.getSanitaryNapkinDistributed());
        dto.setNoOfPacketsDistributed(adolescentHealth.getNoOfPacketsDistributed());
        dto.setPlace(adolescentHealth.getPlace());
        dto.setReferredToHealthFacility(adolescentHealth.getReferredToHealthFacility());
        dto.setCounselingProvided(adolescentHealth.getCounselingProvided());
        dto.setCounselingType(adolescentHealth.getCounselingType());
        dto.setFollowUpDate(adolescentHealth.getFollowUpDate());
        dto.setReferralStatus(adolescentHealth.getReferralStatus());
        return dto;
    }

    private void checkAndAddIncentives(AdolescentHealth adolescentHealth) {
        if (adolescentHealth == null || adolescentHealth.getBenId() == null) {
            return;
        }

        IncentiveActivity sellingSanitaryActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("SELLING_SANITARY", INCENTIVE_GROUP);
        IncentiveActivity mobilizingADHActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("MOBILIZING_ADOLESCENTS", INCENTIVE_GROUP);

        if (sellingSanitaryActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(sellingSanitaryActivity.getId(), adolescentHealth.getCreatedDate(), adolescentHealth.getBenId().longValue());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(sellingSanitaryActivity.getId());
                record.setCreatedDate(adolescentHealth.getCreatedDate());
                record.setCreatedBy(adolescentHealth.getCreatedBy());
                record.setStartDate(adolescentHealth.getCreatedDate());
                record.setEndDate(adolescentHealth.getCreatedDate());
                record.setUpdatedDate(adolescentHealth.getCreatedDate());
                record.setUpdatedBy(adolescentHealth.getCreatedBy());
                record.setBenId(adolescentHealth.getBenId().longValue());
                record.setAshaId(adolescentHealth.getUserId());
                record.setName(sellingSanitaryActivity.getName());
                // Validate packets number before calculation
                Integer packetsDistributed = adolescentHealth.getNoOfPacketsDistributed();
                long rate = sellingSanitaryActivity.getRate() != null ? sellingSanitaryActivity.getRate() : 0;
                long amount = rate * (packetsDistributed != null ? packetsDistributed : 0);
                record.setAmount(amount);
                recordRepo.save(record);
            }
        }


        if (mobilizingADHActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(mobilizingADHActivity.getId(), adolescentHealth.getCreatedDate(), adolescentHealth.getBenId().longValue());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(mobilizingADHActivity.getId());
                record.setCreatedDate(adolescentHealth.getCreatedDate());
                record.setCreatedBy(adolescentHealth.getCreatedBy());
                record.setStartDate(adolescentHealth.getCreatedDate());
                record.setEndDate(adolescentHealth.getCreatedDate());
                record.setUpdatedDate(adolescentHealth.getCreatedDate());
                record.setUpdatedBy(adolescentHealth.getCreatedBy());
                record.setBenId(adolescentHealth.getBenId().longValue());
                record.setAshaId(adolescentHealth.getUserId());
                record.setName(mobilizingADHActivity.getName());
                record.setAmount(Long.valueOf(mobilizingADHActivity.getRate()) * adolescentHealth.getNoOfPacketsDistributed());
                recordRepo.save(record);
            }
        }
    }
}

