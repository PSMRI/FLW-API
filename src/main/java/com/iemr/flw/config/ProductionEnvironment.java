package com.iemr.flw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Reads is.production (IS_PRODUCTION env variable, set in common_*.properties) once at startup.
// Read as a String, not a boolean: a CI build without IS_PRODUCTION leaves the literal
// "@env.IS_PRODUCTION@" in the property, and a boolean @Value would fail bean creation and stop the
// app. Only an explicit "true" means production; anything else is treated as false, with a warning.
@Component
public class ProductionEnvironment {

    private static final Logger logger = LoggerFactory.getLogger(ProductionEnvironment.class);

    private final boolean production;

    public ProductionEnvironment(@Value("${is.production:false}") String rawValue) {
        String value = rawValue == null ? "" : rawValue.trim();
        this.production = "true".equalsIgnoreCase(value);
        if (!production && !"false".equalsIgnoreCase(value)) {
            logger.warn("is.production has unexpected value '{}' (IS_PRODUCTION not set?) — treating it as false; "
                    + "diagnostic vendor polling stays ON", value);
        }
        logger.info("is.production={} — diagnostic vendor polling is {}", production, production ? "OFF" : "ON");
    }

    public boolean isProduction() {
        return production;
    }
}
