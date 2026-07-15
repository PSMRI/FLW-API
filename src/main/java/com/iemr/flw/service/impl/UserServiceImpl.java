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

    @Autowired
    private FacilityDataService facilityDataService;

    public UserServiceRoleDTO getUserDetail(Integer userId) {
        logger.info("calling getUserRole for userId: " + userId);
        UserServiceRoleDTO userRole = userServiceRoleRepo.getUserRole(userId).get(0);
        userRole.setFacilityData(facilityDataService.buildFacilityData(userId, userRole.getRoleName()));

        // Stop TB / Nikshay — additive only. This naturally returns nothing for
        // any user whose rows don't have NikshayTUID set, i.e. every non-Stop-TB
        // user. Fetched first so the district-by-block patch below can tell
        // Stop TB users apart and skip them.
        java.util.List<Object[]> nikshayResults = userServiceRoleRepo.getNikshayLocationScope(userId, userRole.getProviderServiceMapId());
        boolean isStopTBUser = nikshayResults != null && !nikshayResults.isEmpty()
                && nikshayResults.get(0) != null && nikshayResults.get(0).length == 4
                && nikshayResults.get(0)[0] != null;

        // Stop TB's BlockId holds a Nikshay TU ID, not an AMRIT BlockID -
        // joining it against m_districtblock/m_district (what
        // getDistrictByBlockId does) would resolve to whatever AMRIT
        // district happens to share that same numeric ID by coincidence,
        // not real data. Skip this patch for Stop TB users.
        if (!isStopTBUser && userRole.getWorkingDistrictId() == null && userRole.getBlockId() != null) {
            java.util.List<Object[]> districtResults = userServiceRoleRepo.getDistrictByBlockId(userRole.getBlockId());
            if (districtResults != null && !districtResults.isEmpty()) {
                Object[] district = districtResults.get(0);
                if (district != null && district.length == 2) {
                    userRole.setWorkingDistrictId(((Number) district[0]).intValue());
                    userRole.setWorkingDistrictName((String) district[1]);
                }
            }
        }

        if (isStopTBUser) {
            Object[] nikshay = nikshayResults.get(0);
            userRole.setTuId((String) nikshay[0]);
            userRole.setTuName((String) nikshay[1]);
            userRole.setHealthFacilityId((String) nikshay[2]);
            userRole.setHealthFacilityName((String) nikshay[3]);
        }

        return userRole;
    }
}
