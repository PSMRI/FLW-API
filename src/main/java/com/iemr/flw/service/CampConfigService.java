package com.iemr.flw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Camp/van identity for this deployment.
 *
 * Previously read from Redis ("camp:vanID"), written at MMU login and deleted (globally,
 * unscoped) on ANY user's logout — a Redis outage or an unrelated user's logout would silently
 * break every Stop TB save on this camp. Each camp/van already runs its own dedicated backend
 * instance against its own local DB, so which van this is never actually changes at runtime —
 * it's a property of the deployment, not a login-time session value. Reading it from properties
 * instead removes the Redis dependency entirely and fixes the global-key bug as a side effect
 * (see stoptb-camp-vanid-global-key-bug investigation).
 *
 * No inline default — every properties file must set stoptb.van.id explicitly, so a forgotten
 * config fails loudly at startup instead of silently running unconfigured.
 *
 * Scope: vanID (and the VanSerialNo stamping that depends on it) only. parkingPlaceID is out of
 * scope for this change.
 */
@Service
public class CampConfigService {

    @Value("${stoptb.van.id}")
    private int vanID;

    // When true, saves fail loudly if camp is not configured (vanID=0) instead of silently
    // storing vanID=NULL. No inline default — every properties file must set this explicitly.
    @Value("${stoptb.enforce.vanid}")
    private boolean enforceVanID;

    public Integer getVanID() {
        if (vanID <= 0) {
            if (enforceVanID) {
                throw new IllegalStateException(
                    "Camp not configured: stoptb.van.id is 0. Set stoptb.van.id in this deployment's properties file.");
            }
            return null;
        }
        return vanID;
    }

    public boolean isCampConfigured() {
        return vanID > 0;
    }

    // Not sourced from Redis anymore (out of scope for this change) — kept only because
    // callers across StopTBServiceImpl/DiagnosticOrderServiceImpl/etc. still call it.
    public Integer getParkingPlaceID() {
        return 0;
    }
}
