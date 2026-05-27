package com.iemr.flw.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Timestamp convertToTimestamp(String inputDate) {
        try {
            if (inputDate == null || inputDate.isEmpty()) return null;

            // Parse "14-10-2025" → LocalDate
            LocalDate localDate = LocalDate.parse(inputDate, INPUT_FORMAT);

            // Convert to LocalDateTime (midnight)
            LocalDateTime localDateTime = localDate.atStartOfDay();

            // Convert to SQL Timestamp
            return Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            System.err.println("❌ Date parsing failed for: " + inputDate + " | " + e.getMessage());
            return null;
        }
    }
}

