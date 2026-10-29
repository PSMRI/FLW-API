package com.iemr.flw.service;

import java.util.List;
import java.util.Map;

public interface StopTBSyncService {

    /**
     * One page of the STOP-TB changes feed for the given villages: a first full download when the
     * token is missing, from another hub or too old; otherwise only what changed after it.
     * Returns {supported:false} where the change log isn't installed, so the app keeps its full pull.
     * The service and villages must be mapped to userId (from the JWT); empty villageIds means all of
     * the user's villages for that service.
     * types limits the feed to the record types the app applies (StopTBSyncType api names); null or
     * empty means all. The app must start a new snapshot (no token) whenever it changes this list.
     */
    Map<String, Object> getChanges(Integer userId, String token, Integer providerServiceMapID, List<Integer> villageIds,
                                   List<String> types, Integer limit, String authorisation);
}
