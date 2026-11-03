package com.iemr.flw.service.sync;

import java.util.*;

/**
 * The villages a user may sync, per provider service map: every active role-mapping row counts
 * (a user can have several), each holding a comma-separated village list.
 *
 * @author Piramal Swasthya
 */
public final class UserSyncScope {

    private final Map<Integer, Set<Integer>> villagesByService;

    private UserSyncScope(Map<Integer, Set<Integer>> villagesByService) {
        this.villagesByService = villagesByService;
    }

    /** rows: [ProviderServiceMapID, Villageid csv] from m_userservicerolemapping. */
    public static UserSyncScope fromMappingRows(List<Object[]> rows) {
        Map<Integer, Set<Integer>> out = new HashMap<>();
        for (Object[] row : rows) {
            if (row == null || row[0] == null) continue;
            Set<Integer> villages = out.computeIfAbsent(((Number) row[0]).intValue(), k -> new TreeSet<>());
            if (row.length > 1 && row[1] != null) {
                for (String id : row[1].toString().split(",")) {
                    try {
                        villages.add(Integer.valueOf(id.trim()));
                    } catch (NumberFormatException ignored) {
                        // malformed entry: not a village the user can sync
                    }
                }
            }
        }
        return new UserSyncScope(out);
    }

    /**
     * The villages to sync for this request: all of the user's villages for the service when none
     * are asked for, otherwise exactly the ones asked for. Throws when the service or any village
     * isn't mapped to the user.
     */
    public List<Integer> resolve(Integer providerServiceMapID, List<Integer> requested) {
        Set<Integer> allowed = villagesByService.get(providerServiceMapID);
        if (allowed == null) {
            throw new IllegalArgumentException("User is not mapped to providerServiceMapID " + providerServiceMapID);
        }
        if (requested == null || requested.isEmpty()) {
            if (allowed.isEmpty()) throw new IllegalArgumentException("User has no villages for this service");
            return new ArrayList<>(allowed);
        }
        List<Integer> outside = new ArrayList<>();
        for (Integer v : requested) if (!allowed.contains(v)) outside.add(v);
        if (!outside.isEmpty()) {
            throw new IllegalArgumentException("Villages not assigned to the user: " + outside);
        }
        return new ArrayList<>(new TreeSet<>(requested));
    }

    /** A stable fingerprint of a service and its village list, bound into sync tokens. */
    public static long scopeHash(Integer providerServiceMapID, List<Integer> villages) {
        List<Integer> sorted = new ArrayList<>(villages);
        Collections.sort(sorted);
        return Integer.toUnsignedLong((providerServiceMapID + "|" + sorted).hashCode());
    }
}
