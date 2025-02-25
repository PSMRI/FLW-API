package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.AshaProfileRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AshaProfileImpl implements AshaProfileService {
    @Autowired
    AshaProfileRepo ashaProfileRepo;
    @Autowired
    EmployeeMasterInter employeeMasterInter;

    private final Logger logger = LoggerFactory.getLogger(AshaProfileImpl.class);

    @Override
    public AshaWorker saveEditData(AshaWorker ashaWorkerRequest) {
        AshaWorker ashaWorker;
        if (ashaWorkerRequest.getId() != null) {
            ashaWorker = ashaProfileRepo.saveAndFlush(updateProfile(ashaWorkerRequest));

        } else {
            ashaWorker = ashaProfileRepo.saveAndFlush(ashaWorkerRequest);

        }
        System.out.println("ashaWorker->>>" + ashaWorker.toString());

        return ashaWorker;


    }

    @Override
    public AshaWorker getProfileData(Integer employeeId) {
        if (ashaProfileRepo.findByEmployeeId(employeeId)!=null) {
            return ashaProfileRepo.findByEmployeeId(employeeId);
        } else {
            return getDetails(employeeId);
        }
    }

    private AshaWorker getDetails(Integer userID) {
        AshaWorker ashaWorker = new AshaWorker();
        M_User m_user = employeeMasterInter.getUserDetails(userID);

        ashaWorker.setEmployeeId(m_user.getUserID());
        ashaWorker.setEmployeeId(m_user.getUserID());
        ashaWorker.setDob(m_user.getDOB());
        ashaWorker.setDateOfJoining(m_user.getDOJ());
        ashaWorker.setName(m_user.getFirstName() + " " + m_user.getLastName());
        ashaWorker.setMobileNumber(m_user.getContactNo());
        ashaWorker.setAlternateMobileNumber(m_user.getEmergencyContactNo());
        ashaWorker.setProviderServiceMapID(m_user.getServiceProviderID());
        return ashaWorker;
    }


    private AshaWorker updateProfile(AshaWorker editEmployee) {
        System.out.println(editEmployee.toString());

        AshaWorker editdata = new AshaWorker();
        editdata.setId(editEmployee.getId());
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
        editdata.setChoName(editEmployee.getChoName());
        editdata.setChoMobile(editEmployee.getChoMobile());
        editdata.setAbhaNumber(editEmployee.getAbhaNumber());
        editdata.setAshaFamilyMember(editEmployee.getAshaFamilyMember());
        editdata.setDateOfJoining(editEmployee.getDateOfJoining());
        editdata.setMobileNumber(editEmployee.getMobileNumber());
        editdata.setAshaHouseholdRegistration(editEmployee.getAshaHouseholdRegistration());
        editdata.setFatherOrSpouseName(editEmployee.getFatherOrSpouseName());
        editdata.setPopulationCovered(editEmployee.getPopulationCovered());
        editdata.setAnm1Name(editEmployee.getAnm1Name());
        editdata.setAnm2Mobile(editEmployee.getAnm2Mobile());  // Corrected line
        editdata.setAwwMobile(editEmployee.getAwwMobile());
        editdata.setProviderServiceMapID(editEmployee.getProviderServiceMapID());

        return editdata;


    }

}
