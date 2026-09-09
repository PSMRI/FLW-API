package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

        // Two different villages can legitimately share the same name (confirmed live:
        // Nikshay village IDs 275135 and 275136 are both named "Sanapindapadar", under
        // two different facilities in Boriguma block, Koraput). The mobile app's village
        // dropdown displays villageName only, so a user has no way to tell which one
        // they're picking - selecting the wrong one loads zero HH records for that
        // village, since worklists filter by ID, not name. Append the ID into each name
        // segment so duplicates are visually distinguishable. Response shape is
        // unchanged - still two parallel comma-separated strings - so this needs zero
        // mobile app changes: it just displays whatever text is in the name string
        // (confirmed against ServiceLocationViewModel/BindingUtils.setSpinnerItems -
        // plain ArrayAdapter, selection is by array position, not by name content).
        userRole.setVillageName(appendVillageIds(userRole.getVillageId(), userRole.getVillageName()));

        // Stop TB / Nikshay — additive only. This naturally returns nothing for
        // any user whose rows don't have NikshayTUID set, i.e. every non-Stop-TB
        // user. Fetched first so the district-by-block patch below can tell
        // Stop TB users apart and skip them.
        List<Object[]> nikshayRows = userServiceRoleRepo.getNikshayMappingRows(userId, userRole.getProviderServiceMapId());
        boolean isStopTBUser = nikshayRows != null && !nikshayRows.isEmpty();

        // Stop TB's BlockId holds a Nikshay TU ID, not an AMRIT BlockID -
        // joining it against m_districtblock/m_district (what
        // getDistrictByBlockId does) would resolve to whatever AMRIT
        // district happens to share that same numeric ID by coincidence,
        // not real data. Skip this patch for Stop TB users.
        if (!isStopTBUser && userRole.getWorkingDistrictId() == null && userRole.getBlockId() != null) {
            List<Object[]> districtResults = userServiceRoleRepo.getDistrictByBlockId(userRole.getBlockId());
            if (districtResults != null && !districtResults.isEmpty()) {
                Object[] district = districtResults.get(0);
                if (district != null && district.length == 2) {
                    userRole.setWorkingDistrictId(((Number) district[0]).intValue());
                    userRole.setWorkingDistrictName((String) district[1]);
                }
            }
        }

        if (isStopTBUser) {
            TreeSet<Integer> tuIds = new TreeSet<>();
            TreeSet<Integer> facilityIds = new TreeSet<>();
            Integer districtId = null;
            for (Object[] row : nikshayRows) {
                addCsvIds((String) row[0], tuIds);
                addCsvIds((String) row[1], facilityIds);
                if (districtId == null && row[2] != null) {
                    districtId = ((Number) row[2]).intValue();
                }
            }

            Map<Integer, String> tuNames = tuIds.isEmpty() ? Map.of()
                    : toIdNameMap(userServiceRoleRepo.findNikshayTuNames(tuIds));
            Map<Integer, String> facilityNames = facilityIds.isEmpty() ? Map.of()
                    : toIdNameMap(userServiceRoleRepo.findNikshayFacilityNames(facilityIds));

            userRole.setTuId(joinIds(tuIds));
            userRole.setTuName(joinNames(tuIds, tuNames));
            userRole.setHealthFacilityId(joinIds(facilityIds));
            userRole.setHealthFacilityName(joinNames(facilityIds, facilityNames));

            // Stop TB never sets WorkingLocationID, so workingDistrictId/Name
            // (derived from it) are always null. DistrictID sits directly on
            // the mapping row instead - Stop TB has no "work location"
            // indirection layer the way other servicelines do, so read it
            // straight from there rather than trying to derive it.
            if (districtId != null) {
                userRole.setWorkingDistrictId(districtId);
                userRole.setWorkingDistrictName(userServiceRoleRepo.findNikshayDistrictName(districtId));
            }
        }

        return userRole;
    }

    // Zips the parallel villageId/villageName comma lists and rejoins each pair as
    // "name (id)", e.g. "Sanapindapadar,Sanapindapadar" + "275135,275136" becomes
    // "Sanapindapadar (275135),Sanapindapadar (275136)". Falls back to the original,
    // unmodified villageName on any shape mismatch (null/blank, or unequal segment
    // counts between the two lists) rather than risk emitting a malformed string.
    private String appendVillageIds(String villageId, String villageName) {
        if (villageId == null || villageId.isBlank() || villageName == null || villageName.isBlank()) {
            return villageName;
        }
        String[] ids = villageId.split(",", -1);
        String[] names = villageName.split(",", -1);
        if (ids.length != names.length) {
            logger.warn("villageId/villageName segment count mismatch ({} vs {}); leaving villageName unmodified",
                    ids.length, names.length);
            return villageName;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(names[i].trim()).append(" (").append(ids[i].trim()).append(")");
        }
        return result.toString();
    }

    private void addCsvIds(String csv, Collection<Integer> target) {
        if (csv == null || csv.isBlank()) {
            return;
        }
        for (String id : csv.split(",")) {
            try {
                target.add(Integer.valueOf(id.trim()));
            } catch (NumberFormatException ignored) {
                // skip malformed entries rather than fail the whole login
            }
        }
    }

    private Map<Integer, String> toIdNameMap(List<Object[]> rows) {
        Map<Integer, String> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).intValue(), (String) row[1]);
        }
        return map;
    }

    private String joinIds(Collection<Integer> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private String joinNames(Collection<Integer> ids, Map<Integer, String> names) {
        return ids.stream().map(id -> names.getOrDefault(id, "")).collect(Collectors.joining(","));
    }
}
