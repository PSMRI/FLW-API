package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.repo.iemr.AshaProfileRepo;
import com.iemr.flw.service.AshaProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class AshaProfileImpl implements AshaProfileService {
    @Autowired
    AshaProfileRepo ashaProfileRepo;

    private final Logger logger = LoggerFactory.getLogger(AshaProfileImpl.class);

    @Override
    public AshaWorker saveEditData(AshaWorker ashaWorker) {
        AshaWorker ashaWorker1;
        if(Objects.equals(ashaProfileRepo.findByEmployeeId(ashaWorker.getEmployeeId()).getEmployeeId(), ashaWorker.getEmployeeId())){
             ashaWorker1 = ashaProfileRepo.saveAndFlush(updateProfile(ashaWorker));

        }else {
             ashaWorker1 = ashaProfileRepo.saveAndFlush(ashaWorker);

        }
        System.out.println("ashaWorker->>>"+ashaWorker1.toString());

        return   ashaWorker1;


    }

    @Override
    public AshaWorker getProfileData(Integer employeeId) {
        return ashaProfileRepo.findByEmployeeId(employeeId);
    }


    private AshaWorker updateProfile(AshaWorker editEmployee) {
        System.out.println(editEmployee.toString());

        AshaWorker editdata = new AshaWorker();
        editdata.setId(editEmployee.getId());
        editdata.setAge(editEmployee.getAge());
        editdata.setAbhaNumber(editEmployee.getAbhaNumber());
        editdata.setEmployeeId(editEmployee.getEmployeeId());
        editdata.setDob(editEmployee.getDob());
        editdata.setAlternateMobileNumber(editEmployee.getAlternateMobileNumber());
        editdata.setAnm1Mobile(editEmployee.getAnm1Mobile());
        editdata.setAnm2Name(editEmployee.getAnm2Name());
        editdata.setIfsc(editEmployee.getIfsc());
        editdata.setAwwName(editEmployee.getAwwName());
        editdata.setName(editEmployee.getName());
        editdata.setVillage(editEmployee.getVillage());
        editdata.setBankAccount(editEmployee.getBankAccount());
        editdata.setChoMobile(editEmployee.getChoMobile());
        editdata.setAbhaNumber(editEmployee.getAbhaNumber());
        editdata.setAshaFamilyMember(editEmployee.getAshaFamilyMember());
        editdata.setAshaHouseholdRegistration(editEmployee.getAshaHouseholdRegistration());
        editdata.setFatherOrSpouseName(editEmployee.getFatherOrSpouseName());
        editdata.setPopulationCovered(editEmployee.getPopulationCovered());
        editdata.setAnm1Name(editEmployee.getAnm1Name());
        editdata.setAnm2Mobile(editEmployee.getAnm2Mobile());  // Corrected line
        editdata.setAwwMobile(editEmployee.getAwwMobile());

        return editdata;



    }

}
