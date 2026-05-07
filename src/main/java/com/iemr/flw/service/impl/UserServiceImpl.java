package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    public UserServiceRoleDTO getUserDetail(Integer userId) {
        logger.info("calling getUserRole for userId: " + userId);
        UserServiceRoleDTO userRole = userServiceRoleRepo.getUserRole(userId).get(0);
        if (userRole.getWorkingDistrictId() == null && userRole.getBlockId() != null) {
            Object[] district = userServiceRoleRepo.getDistrictByBlockId(userRole.getBlockId());
            if (district != null && district.length == 2) {
                userRole.setWorkingDistrictId(((Number) district[0]).intValue());
                userRole.setWorkingDistrictName((String) district[1]);
            }
        }
        return userRole;
    }
}
