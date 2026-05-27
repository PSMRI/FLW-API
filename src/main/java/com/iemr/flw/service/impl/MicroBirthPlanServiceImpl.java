package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.repo.iemr.MicroBirthPlanRepository;
import com.iemr.flw.service.MicroBirthPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MicroBirthPlanServiceImpl  implements MicroBirthPlanService {
     @Autowired
    private MicroBirthPlanRepository microBirthPlanRepository;


    @Override
    public MicroBirthPlan createMicroBirthPlan(MicroBirthPlanDTO birthPlan) {
        MicroBirthPlan lastProcessedPlan = new MicroBirthPlan();

        for (MicroBirthPlan entry : birthPlan.getEntries()) {
            // Check if the entry already exists in the database
            Optional<MicroBirthPlan> existingEntry = microBirthPlanRepository.findByBenId(entry.getBenId());

            if (existingEntry.isPresent()) {
                lastProcessedPlan=  updateMicroBirthPlan(entry.getBenId(),entry);
                continue;
            }

            // Set user ID and prepare for saving
            entry.setUserId(birthPlan.getUserId());
            lastProcessedPlan = microBirthPlanRepository.save(entry);
        }

        return lastProcessedPlan;
    }


    @Override
    public List<MicroBirthPlan> getAllMicroBirthPlans(Integer userId) {
           return microBirthPlanRepository.findByUserId(userId);

    }

    private MicroBirthPlan  updateMicroBirthPlan(Long id, MicroBirthPlan updatedPlan){
        return microBirthPlanRepository.findByBenId(id).map(existingPlan -> {
            existingPlan.setContactNumber1(updatedPlan.getContactNumber1());
            existingPlan.setContactNumber2(updatedPlan.getContactNumber2());
            existingPlan.setScHosp(updatedPlan.getScHosp());
            existingPlan.setBlock(updatedPlan.getBlock());
            existingPlan.setNearestSc(updatedPlan.getNearestSc());
            existingPlan.setNearestPhc(updatedPlan.getNearestPhc());
            existingPlan.setNearestFru(updatedPlan.getNearestFru());
            existingPlan.setUsg(updatedPlan.getUsg());
            existingPlan.setBloodGroup(updatedPlan.getBloodGroup());
            existingPlan.setBloodDonors2(updatedPlan.getBloodDonors2());
            existingPlan.setBirthCompanion(updatedPlan.getBirthCompanion());
            existingPlan.setCareTaker(updatedPlan.getCareTaker());
            existingPlan.setCommunityMember(updatedPlan.getCommunityMember());
            existingPlan.setCommunityMemberContact(updatedPlan.getCommunityMemberContact());
            existingPlan.setModeOfTransportation(updatedPlan.getModeOfTransportation());
            existingPlan.setBenId(updatedPlan.getBenId());
            return microBirthPlanRepository.save(existingPlan);
        }).orElseThrow(() -> new RuntimeException("Micro Birth Plan not found"));
    }


}