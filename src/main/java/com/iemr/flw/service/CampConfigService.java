package com.iemr.flw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class CampConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CampConfigService.class);
    private static final String VAN_ID_KEY = "camp:vanID";
    private static final String PARKING_PLACE_ID_KEY = "camp:parkingPlaceID";

    @Autowired
    private LettuceConnectionFactory connectionFactory;

    // When true, saves fail loudly if camp is not configured instead of silently storing vanID=NULL
    @Value("${stoptb.enforce.vanid:false}")
    private boolean enforceVanID;

    public Integer getVanID() {
        String val = read(VAN_ID_KEY);
        if (val == null || val.isBlank()) {
            if (enforceVanID) {
                throw new IllegalStateException(
                    "Camp not configured: vanID missing. Please select van/service point in MMU before saving Stop TB data.");
            }
            return null;
        }
        return Integer.parseInt(val);
    }

    public Integer getParkingPlaceID() {
        String val = read(PARKING_PLACE_ID_KEY);
        if (val == null || val.isBlank()) return 0;
        return Integer.parseInt(val);
    }

    public boolean isCampConfigured() {
        String val = read(VAN_ID_KEY);
        return val != null && !val.isBlank();
    }

    private String read(String key) {
        try {
            RedisConnection conn = connectionFactory.getConnection();
            byte[] data = conn.get(key.getBytes());
            conn.close();
            return data != null ? new String(data) : null;
        } catch (Exception e) {
            logger.error("Redis read error for key {}: {}", key, e.getMessage());
            return null;
        }
    }
}
