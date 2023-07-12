package com.iemr.flw.service.impl;

import com.iemr.flw.dto.UserServiceRoleDTO;
import com.iemr.flw.repo.UserServiceRoleRepo;
import com.iemr.flw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceRoleDTO getUserRole(Integer userId, Integer roleId) {
        logger.info("calling getUserRole for userId: " + userId);
        UserServiceRoleDTO userRole = userServiceRoleRepo.getUserRole(userId, roleId);
        return userRole;
    }
}
