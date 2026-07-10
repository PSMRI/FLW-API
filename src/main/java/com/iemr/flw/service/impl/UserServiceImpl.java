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
            java.util.List<Object[]> districtResults = userServiceRoleRepo.getDistrictByBlockId(userRole.getBlockId());
            if (districtResults != null && !districtResults.isEmpty()) {
                Object[] district = districtResults.get(0);
                if (district != null && district.length == 2) {
                    userRole.setWorkingDistrictId(((Number) district[0]).intValue());
                    userRole.setWorkingDistrictName((String) district[1]);
                }
            }
        }

        // Stop TB / Nikshay — additive only. This naturally returns nothing for
        // any user whose rows don't have NikshayTUID set, i.e. every non-Stop-TB
        // user, so no explicit service-name check is needed here.
        java.util.List<Object[]> nikshayResults = userServiceRoleRepo.getNikshayLocationScope(userId, userRole.getProviderServiceMapId());
        if (nikshayResults != null && !nikshayResults.isEmpty()) {
            Object[] nikshay = nikshayResults.get(0);
            if (nikshay != null && nikshay.length == 4 && nikshay[0] != null) {
                userRole.setTuId((String) nikshay[0]);
                userRole.setTuName((String) nikshay[1]);
                userRole.setHealthFacilityId((String) nikshay[2]);
                userRole.setHealthFacilityName((String) nikshay[3]);
            }
        }

        return userRole;
    }
}
