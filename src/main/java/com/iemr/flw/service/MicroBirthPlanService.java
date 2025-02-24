package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.repo.iemr.MicroBirthPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MicroBirthPlanService {
     @Autowired
    private  MicroBirthPlanRepository repository;

    public MicroBirthPlan createMicroBirthPlan(MicroBirthPlan birthPlan) {
        return repository.save(birthPlan);
    }

    public List<MicroBirthPlan> getAllMicroBirthPlans() {
        return repository.findAll();
    }

    public Optional<MicroBirthPlan> getMicroBirthPlanById(Long id) {
        return repository.findById(id);
    }

    public MicroBirthPlan updateMicroBirthPlan(Long id, MicroBirthPlan updatedPlan) {
        return repository.findById(id).map(existingPlan -> {
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
            return repository.save(existingPlan);
        }).orElseThrow(() -> new RuntimeException("Micro Birth Plan not found"));
    }

    public void deleteMicroBirthPlan(Long id) {
        repository.deleteById(id);
    }
}
