package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.AshaSupervisorMapping;
import com.iemr.flw.repo.iemr.AshaSupervisorLoginRepo;
import com.iemr.flw.repo.iemr.FacilityLoginRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FacilityDataService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private AshaSupervisorLoginRepo ashaSupervisorLoginRepo;

    @Autowired
    private FacilityLoginRepo facilityLoginRepo;

    public Map<String, Object> buildFacilityData(Integer userID, String roleName) {
        Map<String, Object> result = new HashMap<>();
        result.put("location", emptyLocation());
        try {
            if ("ASHA Supervisor".equalsIgnoreCase(roleName)) {
                enrichAshaSupervisorData(result, userID);
            } else if ("ASHA".equalsIgnoreCase(roleName)) {
                enrichAshaData(result, userID);
            } else {
                enrichGeneralFacilityData(result, userID);
            }
        } catch (Exception e) {
            logger.error("Error building facilityData for userID " + userID + ": " + e.getMessage(), e);
        }
        return result;
    }

    private void enrichAshaSupervisorData(Map<String, Object> result, Integer supervisorUserID) {
        ArrayList<AshaSupervisorMapping> mappings = ashaSupervisorLoginRepo
                .findBySupervisorUserIDAndDeletedFalse(supervisorUserID);
        if (mappings == null || mappings.isEmpty()) return;

        Set<Integer> facilityIDSet = new HashSet<>();
        for (AshaSupervisorMapping m : mappings) {
            if (m.getFacilityID() != null) facilityIDSet.add(m.getFacilityID());
        }
        if (facilityIDSet.isEmpty()) return;
        List<Integer> facilityIDs = new ArrayList<>(facilityIDSet);

        List<Object[]> facilityRows = facilityLoginRepo.getFacilityDetails(facilityIDs);
        if (facilityRows == null || facilityRows.isEmpty()) return;

        result.put("location", buildLocation(facilityRows.get(0)));

        List<Map<String, Object>> facilities = new ArrayList<>();
        for (Object[] row : facilityRows) {
            Map<String, Object> facility = new HashMap<>();
            facility.put("facilityId", row[0]);
            facility.put("facilityName", str(row[1]));
            facility.put("facilityType", str(row[9]));
            facilities.add(facility);
        }
        result.put("facilities", facilities);

        List<Object[]> ashaRows = facilityLoginRepo.getMappedAshasBySupervisor(supervisorUserID);
        List<Map<String, Object>> ashaList = buildAshaList(ashaRows);
        result.put("ashaList", ashaList);
        result.put("totalAshaCount", ashaList.size());
    }

    private void enrichAshaData(Map<String, Object> result, Integer ashaUserID) {
        List<Integer> facilityIDs = facilityLoginRepo.getUserFacilityIDs(ashaUserID);
        if (facilityIDs == null || facilityIDs.isEmpty()) return;

        List<Object[]> facilityRows = facilityLoginRepo.getFacilityDetails(facilityIDs);
        if (facilityRows == null || facilityRows.isEmpty()) return;

        result.put("location", buildLocation(facilityRows.get(0)));

        Object[] row = facilityRows.get(0);
        Map<String, Object> facility = new HashMap<>();
        facility.put("facilityId", row[0]);
        facility.put("facilityName", str(row[1]));
        facility.put("facilityType", str(row[9]));
        result.put("facility", facility);

        List<Object[]> supervisorRows = facilityLoginRepo.getSupervisorForAsha(ashaUserID);
        if (supervisorRows != null && !supervisorRows.isEmpty()) {
            Object[] sRow = supervisorRows.get(0);
            Map<String, Object> supervisor = new HashMap<>();
            supervisor.put("userId", sRow[0]);
            supervisor.put("fullName", fullName(sRow[1], sRow[2]));
            supervisor.put("mobile", str(sRow[3]));
            supervisor.put("employeeId", str(sRow[4]).isEmpty() ? null : str(sRow[4]));
            result.put("supervisor", supervisor);
        } else {
            result.put("supervisor", null);
        }

        List<Object[]> peerRows = facilityLoginRepo.getPeersAtFacility(facilityIDs, ashaUserID);
        List<Map<String, Object>> peers = new ArrayList<>();
        if (peerRows != null) {
            for (Object[] pRow : peerRows) {
                Map<String, Object> peer = new HashMap<>();
                peer.put("userId", pRow[0]);
                peer.put("fullName", fullName(pRow[1], pRow[2]));
                peer.put("role", str(pRow[3]));
                peer.put("employeeId", str(pRow[4]).isEmpty() ? null : str(pRow[4]));
                peer.put("mobile", str(pRow[5]).isEmpty() ? null : str(pRow[5]));
                peers.add(peer);
            }
        }
        result.put("peersAtFacility", peers);
    }

    private void enrichGeneralFacilityData(Map<String, Object> result, Integer userID) {
        List<Integer> facilityIDs = facilityLoginRepo.getUserFacilityIDs(userID);
        if (facilityIDs == null || facilityIDs.isEmpty()) return;

        List<Object[]> facilityRows = facilityLoginRepo.getFacilityDetails(facilityIDs);
        if (facilityRows == null || facilityRows.isEmpty()) return;

        result.put("location", buildLocation(facilityRows.get(0)));

        List<Map<String, Object>> facilities = new ArrayList<>();
        for (Object[] row : facilityRows) {
            Map<String, Object> facility = new HashMap<>();
            facility.put("facilityId", row[0]);
            facility.put("facilityName", str(row[1]));
            facility.put("facilityType", str(row[9]));
            facilities.add(facility);
        }
        result.put("facilities", facilities);

        List<Object[]> ashaRows = facilityLoginRepo.getAshaListByFacilities(facilityIDs);
        List<Map<String, Object>> ashaList = buildAshaList(ashaRows);
        result.put("ashaList", ashaList);
        result.put("totalAshaCount", ashaList.size());
    }

    private List<Map<String, Object>> buildAshaList(List<Object[]> rows) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (rows == null) return list;
        for (Object[] row : rows) {
            Map<String, Object> asha = new HashMap<>();
            asha.put("userId", row[0]);
            asha.put("fullName", fullName(row[1], row[2]));
            asha.put("employeeId", str(row[3]).isEmpty() ? null : str(row[3]));
            asha.put("facilityId", row[4]);
            asha.put("facilityName", str(row[5]));
            asha.put("facilityType", str(row[6]));
            asha.put("mobile", str(row[7]).isEmpty() ? null : str(row[7]));
            list.add(asha);
        }
        return list;
    }

    private Map<String, Object> buildLocation(Object[] facilityRow) {
        Map<String, Object> location = new HashMap<>();
        location.put("state", str(facilityRow[3]));
        location.put("district", str(facilityRow[5]));
        location.put("blockOrUlb", str(facilityRow[7]));
        location.put("locationType", str(facilityRow[8]));
        return location;
    }

    private Map<String, Object> emptyLocation() {
        Map<String, Object> location = new HashMap<>();
        location.put("state", "");
        location.put("district", "");
        location.put("blockOrUlb", "");
        location.put("locationType", "");
        return location;
    }

    private String str(Object val) {
        return val != null ? val.toString() : "";
    }

    private String fullName(Object first, Object last) {
        return (str(first) + " " + str(last)).trim();
    }
}
