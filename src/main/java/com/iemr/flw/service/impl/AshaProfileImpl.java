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
        return ashaProfileRepo.findByEmployeeId(employeeId);
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


    private AshaWorker updateProfile(AshaWorker editAshaWorkerRequest) {
        System.out.println(editAshaWorkerRequest.toString());

        AshaWorker editdata = new AshaWorker();
        editdata.setId(editAshaWorkerRequest.getId());
        editdata.setAbhaNumber(editAshaWorkerRequest.getAbhaNumber());
        editdata.setEmployeeId(editAshaWorkerRequest.getEmployeeId());
        editdata.setDob(editAshaWorkerRequest.getDob());
        editdata.setAlternateMobileNumber(editAshaWorkerRequest.getAlternateMobileNumber());
        editdata.setAnm1Mobile(editAshaWorkerRequest.getAnm1Mobile());
        editdata.setAnm2Name(editAshaWorkerRequest.getAnm2Name());
        editdata.setIfsc(editAshaWorkerRequest.getIfsc());
        editdata.setAwwName(editAshaWorkerRequest.getAwwName());
        editdata.setName(editAshaWorkerRequest.getName());
        editdata.setVillage(editAshaWorkerRequest.getVillage());
        editdata.setBankAccount(editAshaWorkerRequest.getBankAccount());
        editdata.setChoName(editAshaWorkerRequest.getChoName());
        editdata.setChoMobile(editAshaWorkerRequest.getChoMobile());
        editdata.setAbhaNumber(editAshaWorkerRequest.getAbhaNumber());
        editdata.setAshaFamilyMember(editAshaWorkerRequest.getAshaFamilyMember());
        editdata.setDateOfJoining(editAshaWorkerRequest.getDateOfJoining());
        editdata.setMobileNumber(editAshaWorkerRequest.getMobileNumber());
        editdata.setAshaHouseholdRegistration(editAshaWorkerRequest.getAshaHouseholdRegistration());
        editdata.setFatherOrSpouseName(editAshaWorkerRequest.getFatherOrSpouseName());
        editdata.setPopulationCovered(editAshaWorkerRequest.getPopulationCovered());
        editdata.setAnm1Name(editAshaWorkerRequest.getAnm1Name());
        editdata.setAnm2Mobile(editAshaWorkerRequest.getAnm2Mobile());  // Corrected line
        editdata.setAwwMobile(editAshaWorkerRequest.getAwwMobile());
        editdata.setProviderServiceMapID(editAshaWorkerRequest.getProviderServiceMapID());

        return editdata;


    }

}
