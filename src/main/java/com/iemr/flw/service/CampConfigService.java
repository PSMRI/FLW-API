package com.iemr.flw.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
 * The van's parking place and store (facility) come from m_van, the same source MMU uses
 * (MasterVanRepo.getFacilityID), and are read once and cached since the van never changes.
 */
@Service
public class CampConfigService {

    private final Logger logger = LoggerFactory.getLogger(CampConfigService.class);

    @Value("${stoptb.van.id}")
    private int vanID;

    // When true, saves fail loudly if camp is not configured (vanID=0) instead of silently
    // storing vanID=NULL. No inline default — every properties file must set this explicitly.
    @Value("${stoptb.enforce.vanid}")
    private boolean enforceVanID;

    @PersistenceContext(unitName = "db_iemr")
    private EntityManager entityManager;

    private volatile Integer parkingPlaceID;
    private volatile Integer facilityID;

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

    /**
     * The configured van (stoptb.van.id) is always used. A vanID sent by the app is ignored, so data and
     * stock never land on another camp's van/store; a different value is only logged.
     */
    public Integer resolveVanID(Integer requestedVanID) {
        Integer configured = getVanID();
        if (requestedVanID != null && requestedVanID > 0 && !requestedVanID.equals(configured)) {
            logger.warn("Ignoring vanID " + requestedVanID + " from request; using stoptb.van.id " + configured);
        }
        return configured;
    }

    public boolean isCampConfigured() {
        return vanID > 0;
    }

    // Parking place of the configured van (m_van.ParkingPlaceID); 0 when the camp is not configured
    // or the van is not found, which is what callers received before this lookup existed.
    public Integer getParkingPlaceID() {
        if (parkingPlaceID == null) loadVanDetails();
        return parkingPlaceID != null ? parkingPlaceID : 0;
    }

    // Store mapped to the configured van (m_van.FacilityID); null when no store is mapped.
    public Integer getFacilityID() {
        if (facilityID == null) loadVanDetails();
        return facilityID;
    }

    private synchronized void loadVanDetails() {
        if (!isCampConfigured() || (parkingPlaceID != null && facilityID != null)) return;
        try {
            List<?> rows = entityManager.createNativeQuery(
                    "SELECT ParkingPlaceID, FacilityID FROM db_iemr.m_van WHERE VanID = :vanID AND Deleted = false")
                    .setParameter("vanID", vanID)
                    .getResultList();
            if (rows.isEmpty()) {
                logger.warn("stoptb.van.id " + vanID + " not found in m_van");
                return;
            }
            Object[] row = (Object[]) rows.get(0);
            parkingPlaceID = row[0] != null ? ((Number) row[0]).intValue() : null;
            facilityID = row[1] != null ? ((Number) row[1]).intValue() : null;
        } catch (Exception e) {
            logger.error("Cannot read m_van details for vanID " + vanID, e);
        }
    }
}
