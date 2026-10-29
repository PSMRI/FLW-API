package com.iemr.flw.service;

import java.util.List;
import java.util.Map;

public interface StopTBSyncService {

    /**
     * One page of the STOP-TB changes feed for the given villages: a first full download when the
     * token is missing, from another hub or too old; otherwise only what changed after it.
     * Returns {supported:false} where the hub change log isn't installed, so the app keeps its full pull.
     */
    Map<String, Object> getChanges(String token, Integer providerServiceMapID, List<Integer> villageIds,
                                   Integer limit, String authorisation);
}
