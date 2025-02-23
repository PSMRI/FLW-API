package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.repo.iemr.MicroBirthPlanRepository;
import com.iemr.flw.service.MicroBirthPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MicroBirthPlanServiceImpl  implements MicroBirthPlanService {
     @Autowired
    private  MicroBirthPlanRepository repository;


    @Override
    public MicroBirthPlan createMicroBirthPlan(MicroBirthPlanDTO birthPlan) {
        MicroBirthPlan microBirthPlan = new MicroBirthPlan();
        for(int i =0 ;i< birthPlan.getEntries().size();i++){
            microBirthPlan = birthPlan.getEntries().get(i);
            microBirthPlan.setUserId(birthPlan.getUserId());
        }
        return repository.save(microBirthPlan);
    }

    @Override
    public List<MicroBirthPlan> getAllMicroBirthPlans() {
        return repository.findAll();
    }

    @Override
    public Optional<MicroBirthPlan> getMicroBirthPlanById(Long id) {
        return Optional.empty();
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