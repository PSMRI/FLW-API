package com.iemr.flw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Reads is.central.server (IS_CENTRAL_SERVER env variable, set in common_*.properties) once at startup.
// Default false: camp laptops poll the diagnostic vendor. Set it to true on central, so central never polls
// orders that were synced to it from a laptop (the laptop that pushed the order polls it itself).
// Read as a String, not a boolean: a CI build without IS_CENTRAL_SERVER leaves the literal
// "@env.IS_CENTRAL_SERVER@" in the property, and a boolean @Value would fail bean creation and stop the app.
// Only an explicit "true" marks this server as central; anything else keeps the default (false),
// with a warning if it isn't "false".
@Component
public class DiagnosticPollSettings {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSettings.class);

    private final boolean centralServer;

    public DiagnosticPollSettings(@Value("${is.central.server:false}") String rawValue) {
        String value = rawValue == null ? "" : rawValue.trim();
        this.centralServer = "true".equalsIgnoreCase(value);
        if (!centralServer && !"false".equalsIgnoreCase(value)) {
            logger.warn("is.central.server has unexpected value '{}' (IS_CENTRAL_SERVER not set?) — "
                    + "using the default false; diagnostic vendor polling stays ON", value);
        }
        logger.info("is.central.server={} — diagnostic vendor polling is {}", centralServer, centralServer ? "OFF" : "ON");
    }

    public boolean isCentralServer() {
        return centralServer;
    }

    /** Polling runs everywhere except the central server. */
    public boolean isPollEnabled() {
        return !centralServer;
    }
}
