package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import com.iemr.flw.repo.iemr.AdolescentHealthRepo;
import com.iemr.flw.service.AdolescentHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdolescentHealthServiceImpl implements AdolescentHealthService {
    @Autowired
    private AdolescentHealthRepo adolescentHealthRepo;

    @Override
    public String saveAll(AdolescentHealthDTO adolescentHealthDTO) {
        for (AdolescentHealth adolescentHealth : adolescentHealthDTO.getAdolescentHealths()) {
            // Check if a record with the same benId already exists in the database
            Optional<AdolescentHealth> existingRecord = adolescentHealthRepo.findByBenId(adolescentHealth.getBenId());

            if (existingRecord.isPresent()) {
                // If record exists, update the existing one
                updateAdolescentHealth(existingRecord,adolescentHealth);
            } else {
                // If the record does not exist, create a new one
                adolescentHealthRepo.save(adolescentHealth);
            }
        }

        return "Records updated/added successfully!";
    }




    public List<AdolescentHealth> getAllAdolescentHealth() {
        // Fetch all records from the database
        List<AdolescentHealth> adolescentHealths = adolescentHealthRepo.findAll();

        // Convert the list of entity objects to DTO objects
        return adolescentHealths.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void updateAdolescentHealth(Optional<AdolescentHealth> existingRecord, AdolescentHealth adolescentHealth) {
        AdolescentHealth existingAdolescentHealth = existingRecord.get();

        // Update fields of the existing record with values from the DTO
        existingAdolescentHealth.setUserID(adolescentHealth.getUserID());
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
        existingAdolescentHealth.setIncentiveAmount(adolescentHealth.getIncentiveAmount());

        // Save the updated record back to the database
        adolescentHealthRepo.save(existingAdolescentHealth);
    }

    private AdolescentHealth convertToDTO(AdolescentHealth adolescentHealth) {
        AdolescentHealth dto = new AdolescentHealth();
        dto.setId(adolescentHealth.getId());
        dto.setBenId(adolescentHealth.getBenId());
        dto.setUserID(adolescentHealth.getUserID());
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
        dto.setIncentiveAmount(adolescentHealth.getIncentiveAmount());
        return dto;
    }
}

