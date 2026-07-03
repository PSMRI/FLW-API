package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaWorker;
import com.iemr.flw.domain.iemr.User;
import com.iemr.flw.repo.iemr.AshaProfileRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.AshaProfileService;
import com.iemr.flw.service.EmployeeMasterInter;

import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.utils.JwtAuthenticationUtil;
import com.iemr.flw.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

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
    JwtAuthenticationUtil jwtAuthenticationUtil;
    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;
    private final Logger logger = LoggerFactory.getLogger(AshaProfileImpl.class);

    @Transactional
    @Override
    public AshaWorker saveEditData(AshaWorker request) {
        try {
            Objects.requireNonNull(request, "ASHA worker request must not be null");

            Optional<AshaWorker> ashaWorker = ashaProfileRepo.findByEmployeeId(request.getEmployeeId());

            // ---------- CREATE Case ----------
            if (!ashaWorker.isPresent()) {
                request.setId(null);


                AshaWorker saved = saveProfile(request);
                logger.info("Created ASHA Worker: {}", saved.getId());
                return saved;
            }

            // ---------- UPDATE Case ----------
            AshaWorker updated = updateProfile(request);
            logger.info("Updated ASHA Worker: {}", updated.getId());
            return updated;

        } catch (Exception e) {
            logger.error("Error saving ASHA worker: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save ASHA worker", e);
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
            User m_user = Objects.requireNonNull(employeeMasterInter.getUserDetails(userID), "User details not found for ID: " + userID);
            AshaWorker ashaWorker = new AshaWorker();
            ashaWorker.setEmployeeId(m_user.getUserID());

            ashaWorker.setName(String.format("%s %s", Objects.toString(m_user.getFirstName(), ""), Objects.toString(m_user.getLastName(), "")).trim());
            ashaWorker.setMobileNumber(m_user.getContactNo());
            ashaWorker.setAlternateMobileNumber(m_user.getEmergencyContactNo());
            ashaWorker.setProviderServiceMapID(m_user.getServiceProviderID());
            ashaWorker.setProfileImage(null);
            ashaWorker.setSupervisorName("");
            ashaWorker.setAwwName("");
            ashaWorker.setVillage("");
            ashaWorker.setSupervisorMobile("");
            ashaWorker.setFatherOrSpouseName("");
            ashaWorker.setChoName("");
            ashaWorker.setAnm1Name("");
            ashaWorker.setAnm2Mobile("");
            ashaWorker.setAnm1Mobile("");
            ashaWorker.setBankAccount("");
            ashaWorker.setIfsc("");
            ashaWorker.setIsFatherOrSpouse(false);
            ashaWorker.setPopulationCovered(0);
            ashaWorker.setVillage("");
            ashaWorker.setAwwMobile("");
            ashaWorker.setAnm2Name("");
            ashaWorker.setAbhaNumber("");
            ashaWorker.setAshaHouseholdRegistration("");
            ashaWorker.setAshaFamilyMember("");
            ashaWorker.setId(0L);
            ashaWorker.setChoMobile("");

            return ashaWorker;
        } catch (Exception e) {
            logger.error("Error creating ASHA worker profile from user details for ID {}: {}", userID, e.getMessage(), e);
            throw new RuntimeException("Failed to create ASHA worker profile from user details", e);
        }
    }
    @Transactional
    public AshaWorker saveProfile(AshaWorker request) {
        logger.info("Saving ASHA Profile for EmployeeId: {}", request.getEmployeeId());

        logger.info("Received DOB: {}", request.getDob());
        logger.info("Received Date Of Joining: {}", request.getDateOfJoining());

        AshaWorker existing = ashaProfileRepo
                .findByEmployeeId(request.getEmployeeId())
                .orElse(new AshaWorker());

        if (isValid(request.getName()))
            existing.setName(request.getName());

        if (isValid(request.getVillage()))
            existing.setVillage(request.getVillage());

        if (request.getEmployeeId() != null)
            existing.setEmployeeId(request.getEmployeeId());

        // Save even if null
           existing.setDob(request.getDob());

        if (isValid(request.getMobileNumber()))
            existing.setMobileNumber(request.getMobileNumber());

        if (isValid(request.getAlternateMobileNumber()))
            existing.setAlternateMobileNumber(request.getAlternateMobileNumber());

        if (isValid(request.getFatherOrSpouseName()))
            existing.setFatherOrSpouseName(request.getFatherOrSpouseName());

        // Save even if null
        existing.setDateOfJoining(request.getDateOfJoining());

        if (isValid(request.getBankAccount()))
            existing.setBankAccount(request.getBankAccount());

        if (isValid(request.getIfsc()))
            existing.setIfsc(request.getIfsc());

        if (request.getPopulationCovered() != null)
            existing.setPopulationCovered(request.getPopulationCovered());

        if (isValid(request.getChoName()))
            existing.setChoName(request.getChoName());

        if (isValid(request.getChoMobile()))
            existing.setChoMobile(request.getChoMobile());

        if (isValid(request.getAwwName()))
            existing.setAwwName(request.getAwwName());

        if (isValid(request.getAwwMobile()))
            existing.setAwwMobile(request.getAwwMobile());

        if (isValid(request.getAnm1Name()))
            existing.setAnm1Name(request.getAnm1Name());

        if (isValid(request.getAnm1Mobile()))
            existing.setAnm1Mobile(request.getAnm1Mobile());

        if (isValid(request.getAnm2Name()))
            existing.setAnm2Name(request.getAnm2Name());

        if (isValid(request.getAnm2Mobile()))
            existing.setAnm2Mobile(request.getAnm2Mobile());

        if (isValid(request.getAbhaNumber()))
            existing.setAbhaNumber(request.getAbhaNumber());

        if (isValid(request.getAshaHouseholdRegistration()))
            existing.setAshaHouseholdRegistration(request.getAshaHouseholdRegistration());

        if (isValid(request.getAshaFamilyMember()))
            existing.setAshaFamilyMember(request.getAshaFamilyMember());

        if (request.getProviderServiceMapID() != null)
            existing.setProviderServiceMapID(request.getProviderServiceMapID());

        if (request.getProfileImage() != null && request.getProfileImage().length > 0)
            existing.setProfileImage(request.getProfileImage());

        if (request.getIsFatherOrSpouse() != null)
            existing.setIsFatherOrSpouse(request.getIsFatherOrSpouse());

        if (isValid(request.getSupervisorName()))
            existing.setSupervisorName(request.getSupervisorName());

        if (isValid(request.getSupervisorMobile()))
            existing.setSupervisorMobile(request.getSupervisorMobile());

        return ashaProfileRepo.save(existing);
    }



        public AshaWorker updateProfile(AshaWorker request) {
        AshaWorker existing = ashaProfileRepo.findByEmployeeId(request.getEmployeeId()).orElseThrow(() -> new RuntimeException("ASHA worker not found"));
        if (isValid(request.getAbhaNumber())) existing.setAbhaNumber(request.getAbhaNumber());
        if (request.getEmployeeId() != null) existing.setEmployeeId(request.getEmployeeId());
        if (request.getDob() != null) existing.setDob(request.getDob());
        if (isValid(request.getAlternateMobileNumber()))
            existing.setAlternateMobileNumber(request.getAlternateMobileNumber());
        if (isValid(request.getAnm1Mobile())) existing.setAnm1Mobile(request.getAnm1Mobile());
        if (isValid(request.getAnm2Name())) existing.setAnm2Name(request.getAnm2Name());
        if (isValid(request.getIfsc())) existing.setIfsc(request.getIfsc());
        if (isValid(request.getAwwName())) existing.setAwwName(request.getAwwName());
        if (isValid(request.getName())) existing.setName(request.getName());
        if (isValid(request.getVillage())) existing.setVillage(request.getVillage());
        if (isValid(request.getBankAccount())) existing.setBankAccount(request.getBankAccount());
        if (isValid(request.getChoName())) existing.setChoName(request.getChoName());
        if (isValid(request.getChoMobile())) existing.setChoMobile(request.getChoMobile());
        if (isValid(request.getAshaFamilyMember())) existing.setAshaFamilyMember(request.getAshaFamilyMember());
        if (request.getDateOfJoining() != null) existing.setDateOfJoining(request.getDateOfJoining());
        if (isValid(request.getMobileNumber())) existing.setMobileNumber(request.getMobileNumber());
        if (isValid(request.getAshaHouseholdRegistration()))
            existing.setAshaHouseholdRegistration(request.getAshaHouseholdRegistration());
        if (isValid(request.getFatherOrSpouseName())) existing.setFatherOrSpouseName(request.getFatherOrSpouseName());
        if (request.getPopulationCovered() != null) existing.setPopulationCovered(request.getPopulationCovered());
        if (isValid(request.getAnm1Name())) existing.setAnm1Name(request.getAnm1Name());
        if (isValid(request.getAnm2Mobile())) existing.setAnm2Mobile(request.getAnm2Mobile());
        if (isValid(request.getAwwMobile())) existing.setAwwMobile(request.getAwwMobile());
        if (request.getProviderServiceMapID() != null)
            existing.setProviderServiceMapID(request.getProviderServiceMapID());
        if (isValidImage(request.getProfileImage())) existing.setProfileImage(request.getProfileImage());
        if (request.getIsFatherOrSpouse() != null) existing.setIsFatherOrSpouse(request.getIsFatherOrSpouse());
        if (isValid(request.getSupervisorName())) existing.setSupervisorName(request.getSupervisorName());
        if (isValid(request.getSupervisorMobile())) existing.setSupervisorMobile(request.getSupervisorMobile());
        return ashaProfileRepo.save(existing);
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty() && !"null".equalsIgnoreCase(value.trim());
    }

    private boolean isValidImage(byte[] value) {
        return value != null && value.length > 0;
    }


}
