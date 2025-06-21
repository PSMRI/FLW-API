package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.service.EmployeeMasterInter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeMasterImpl implements EmployeeMasterInter {
    @Autowired
    EmployeeMasterRepo employeeMasterRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    @Override
    public M_User getUserDetails(Integer userID) {
        logger.debug("Fetching user details for userID: {}", userID);
        try {
            if (userID == null) {
                throw new IllegalArgumentException("UserID cannot be null");
            }
            return employeeMasterRepo.findByUserID(userID);
        } catch (Exception e) {
            logger.error("Error fetching user details for userID: {}", userID, e);
            throw e;
        }
    }
}
