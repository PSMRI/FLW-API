package com.iemr.flw.masterEnum;

public enum DiagnosticOrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    EXPIRED,
    REFUSED,
    MANUAL_ENTRY;

    public static DiagnosticOrderStatus fromString(String value) {
        if (value == null) return PENDING;
        for (DiagnosticOrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
