package com.iemr.flw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Reads diagnostic.poll.enabled (DIAGNOSTIC_POLL_ENABLED env variable, set in common_*.properties) once
// at startup. Polling is ON by default; set it to false on central, so central never polls orders that
// were synced to it from a laptop (the laptop that pushed the order polls it itself).
// Read as a String, not a boolean: a CI build without DIAGNOSTIC_POLL_ENABLED leaves the literal
// "@env.DIAGNOSTIC_POLL_ENABLED@" in the property, and a boolean @Value would fail bean creation and
// stop the app. Only an explicit "false" turns polling off; anything else keeps the default (true),
// with a warning if it isn't "true".
@Component
public class DiagnosticPollSettings {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPollSettings.class);

    private final boolean pollEnabled;

    public DiagnosticPollSettings(@Value("${diagnostic.poll.enabled:true}") String rawValue) {
        String value = rawValue == null ? "" : rawValue.trim();
        this.pollEnabled = !"false".equalsIgnoreCase(value);
        if (pollEnabled && !"true".equalsIgnoreCase(value)) {
            logger.warn("diagnostic.poll.enabled has unexpected value '{}' (DIAGNOSTIC_POLL_ENABLED not set?) — "
                    + "using the default true; diagnostic vendor polling stays ON", value);
        }
        logger.info("diagnostic.poll.enabled={} — diagnostic vendor polling is {}", pollEnabled, pollEnabled ? "ON" : "OFF");
    }

    public boolean isPollEnabled() {
        return pollEnabled;
    }
}
