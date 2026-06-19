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
        System.out.println("DEBUG getUserDetail: userId=" + userId
                + " workingDistrictId(fromView)=" + userRole.getWorkingDistrictId()
                + " blockId=" + userRole.getBlockId());
        if (userRole.getWorkingDistrictId() == null && userRole.getBlockId() != null) {
            System.out.println("DEBUG getUserDetail: fallback TRIGGERED, calling getDistrictByBlockId(" + userRole.getBlockId() + ")");
            java.util.List<Object[]> districtResults = userServiceRoleRepo.getDistrictByBlockId(userRole.getBlockId());
            System.out.println("DEBUG getUserDetail: getDistrictByBlockId result = " + districtResults);
            if (districtResults != null && !districtResults.isEmpty()) {
                Object[] district = districtResults.get(0);
                if (district != null && district.length == 2) {
                    userRole.setWorkingDistrictId(((Number) district[0]).intValue());
                    userRole.setWorkingDistrictName((String) district[1]);
                    System.out.println("DEBUG getUserDetail: PATCHED workingDistrictId=" + userRole.getWorkingDistrictId()
                            + " workingDistrictName=" + userRole.getWorkingDistrictName());
                }
            }
        } else {
            System.out.println("DEBUG getUserDetail: fallback SKIPPED (workingDistrictId already set, or blockId null)");
        }
        return userRole;
    }
}
