package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.AshaProfileRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;

import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.utils.JwtAuthenticationUtil;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AshaProfileImpl implements AshaProfileService {
    @Autowired
    AshaProfileRepo ashaProfileRepo;
    @Autowired
    EmployeeMasterInter employeeMasterInter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmployeeMasterRepo userLoginRepo;
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtAuthenticationUtil jwtAuthenticationUtil ;

    private final Logger logger = LoggerFactory.getLogger(AshaProfileImpl.class);

    @Transactional
    @Override
    public AshaWorker saveEditData(AshaWorker ashaWorkerRequest) {
        try {
            Objects.requireNonNull(ashaWorkerRequest, "ashaWorker must not be null");
            AshaWorker savedWorker = ashaWorkerRequest.getId() != null
                    ? ashaProfileRepo.saveAndFlush(updateProfile(ashaWorkerRequest))
                    : ashaProfileRepo.saveAndFlush(ashaWorkerRequest);
            logger.info("ASHA worker profile saved successfully: {}", savedWorker);
            return savedWorker;
        } catch (Exception e) {
            logger.error("Error saving ASHA worker profile: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save ASHA worker profile", e);
        }

    }



    @Override
    public AshaWorker getProfileData(Integer userId) {

        try {

            Objects.requireNonNull(userId, "employeeId must not be null");
            return ashaProfileRepo.findByEmployeeId(userId)
                    .orElseGet(() -> {
                        return getDetails(userId);
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve ASHA worker profile", e);
        }
    }

    private AshaWorker getDetails(Integer userID) {

        try {
            M_User m_user = Objects.requireNonNull(
                    employeeMasterInter.getUserDetails(userID),
                    "User details not found for ID: " + userID
            );

            AshaWorker ashaWorker = new AshaWorker();
            ashaWorker.setEmployeeId(m_user.getUserID());
            ashaWorker.setDob(m_user.getDOB());
            ashaWorker.setDateOfJoining(m_user.getDOJ());
            ashaWorker.setName(String.format("%s %s",
                    Objects.toString(m_user.getFirstName(), ""),
                    Objects.toString(m_user.getLastName(), "")).trim());
            ashaWorker.setMobileNumber(m_user.getContactNo());
            ashaWorker.setAlternateMobileNumber(m_user.getEmergencyContactNo());
            ashaWorker.setProviderServiceMapID(m_user.getServiceProviderID());
            return ashaWorker;
        } catch (Exception e) {
            logger.error("Error creating ASHA worker profile from user details for ID {}: {}", userID, e.getMessage(), e);
            throw new RuntimeException("Failed to create ASHA worker profile from user details", e);
        }
    }


    private AshaWorker updateProfile(AshaWorker editAshaWorkerRequest) {
        System.out.println(editAshaWorkerRequest.toString());
        try {
            Objects.requireNonNull(editAshaWorkerRequest, "editEmployee must not be null");
            logger.debug("Updating ASHA worker profile: {}", editAshaWorkerRequest);
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
            editdata.setProfileImage(editAshaWorkerRequest.getProfileImage());
            editdata.setIsFatherOrSpouse(editAshaWorkerRequest.getIsFatherOrSpouse());
            editdata.setSupervisorName(editAshaWorkerRequest.getSupervisorName());
            editdata.setSupervisorMobile(editAshaWorkerRequest.getSupervisorMobile());

            return editdata;

        } catch (Exception e) {
            logger.error("Error creating updated ASHA worker profile: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create updated ASHA worker profile", e);

        }


    }


}
