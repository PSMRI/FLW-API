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
        MicroBirthPlan microBirthPlan = null;

        for (MicroBirthPlan entry : birthPlan.getEntries()) {
            // Check if the entry already exists in the database
            Optional<MicroBirthPlan> existingEntry = microBirthPlanRepository.findById(entry.getId());

            if (existingEntry.isPresent()) {
                updateMicroBirthPlan(entry.getId(),microBirthPlan);
                continue;
            }

            // Set user ID and prepare for saving
            entry.setUserId(birthPlan.getUserId());
            microBirthPlan = microBirthPlanRepository.save(entry);
        }

        return microBirthPlan;
    }


    @Override
    public List<MicroBirthPlan> getAllMicroBirthPlans(Integer userId) {
        return microBirthPlanRepository.findAll().stream().filter(microBirthPlan -> Objects.equals(microBirthPlan.getUserId(), userId)).collect(Collectors.toList());
    }

    @Override
    public Optional<MicroBirthPlan> getMicroBirthPlanById(Integer id) {
        return Optional.empty();
    }




    private MicroBirthPlan  updateMicroBirthPlan(Integer id, MicroBirthPlan updatedPlan){
        return microBirthPlanRepository.findById(id).map(existingPlan -> {
            existingPlan.setPwName(updatedPlan.getPwName());
            existingPlan.setAge(updatedPlan.getAge());
            existingPlan.setContactNo1(updatedPlan.getContactNo1());
            existingPlan.setContactNo2(updatedPlan.getContactNo2());
            existingPlan.setScHwcTgHosp(updatedPlan.getScHwcTgHosp());
            existingPlan.setBlock(updatedPlan.getBlock());
            existingPlan.setHusbandName(updatedPlan.getHusbandName());
            existingPlan.setNearestScHwc(updatedPlan.getNearestScHwc());
            existingPlan.setNearestPhc(updatedPlan.getNearestPhc());
            existingPlan.setNearestFru(updatedPlan.getNearestFru());
            existingPlan.setNearestUsg(updatedPlan.getNearestUsg());
            existingPlan.setBloodGroup(updatedPlan.getBloodGroup());
            existingPlan.setBloodDonors(updatedPlan.getBloodDonors());
            existingPlan.setBirthCompanion(updatedPlan.getBirthCompanion());
            existingPlan.setChildCaretaker(updatedPlan.getChildCaretaker());
            existingPlan.setCommunitySupport(updatedPlan.getCommunitySupport());
            existingPlan.setTransportationMode(updatedPlan.getTransportationMode());
            existingPlan.setIsSynced(true);
            existingPlan.setBenId(updatedPlan.getBenId());
            return microBirthPlanRepository.save(existingPlan);
        }).orElseThrow(() -> new RuntimeException("Micro Birth Plan not found"));
    }

    public void deleteMicroBirthPlan(Integer id) {
        microBirthPlanRepository.deleteById(id);
    }
}