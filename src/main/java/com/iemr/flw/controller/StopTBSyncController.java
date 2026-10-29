package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.service.StopTBSyncService;
import com.iemr.flw.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stoptb/sync")
public class StopTBSyncController {

    private final Logger logger = LoggerFactory.getLogger(StopTBSyncController.class);

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Autowired
    private StopTBSyncService stopTBSyncService;

    /**
     * Body: { token: string|null, providerServiceMapID: int, villageIds: [int], types: [string], limit: int }.
     * types: the record types the app applies (all when omitted); changing it requires a new snapshot.
     * Response: { supported, serverId, snapshot, items: [{type, op, key, version, data}], nextToken, hasMore }.
     * Call again with nextToken while hasMore is true. On a server without the change log (AMRIT-DB before V132) the
     * response is { supported: false } and the app keeps using the getAll endpoints.
     */
    @PostMapping("/changes")
    @Operation(summary = "STOP-TB records changed since the token (first call: everything in the villages)")
    public String getChanges(@RequestBody Map<String, Object> body,
                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        OutputResponse response = new OutputResponse();
        try {
            Object token = body.get("token");
            Integer psm = body.get("providerServiceMapID") != null
                    ? Integer.parseInt(body.get("providerServiceMapID").toString()) : null;
            List<Integer> villageIds = new ArrayList<>();
            if (body.get("villageIds") instanceof List) {
                for (Object v : (List<?>) body.get("villageIds")) {
                    if (v != null) villageIds.add((int) Double.parseDouble(v.toString()));
                }
            }
            List<String> types = new ArrayList<>();
            if (body.get("types") instanceof List) {
                for (Object t : (List<?>) body.get("types")) {
                    if (t != null) types.add(t.toString());
                }
            }
            Integer limit = body.get("limit") != null ? (int) Double.parseDouble(body.get("limit").toString()) : null;
            Map<String, Object> data = stopTBSyncService.getChanges(
                    token != null ? token.toString() : null, psm, villageIds, types, limit, authorization);
            response.setResponse(gson.toJson(data));
        } catch (Exception e) {
            logger.error("Error in sync/changes: " + e);
            response.setError(5000, "Error fetching changes: " + e.getMessage());
        }
        return response.toString();
    }
}
