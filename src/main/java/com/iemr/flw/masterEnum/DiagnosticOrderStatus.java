package com.iemr.flw.masterEnum;

public enum DiagnosticOrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CLOSED,
    MANUAL_ENTRY;

    public static DiagnosticOrderStatus fromString(String value) {
        if (value == null) return PENDING;
        // IN_PROGRESS is kept only as a legacy/historical value (old rows, backward-compat reporting
        // queries) — it is never freshly assigned. A vendor reporting "IN_PROGRESS" is just PENDING.
        if (IN_PROGRESS.name().equalsIgnoreCase(value)) return PENDING;
        for (DiagnosticOrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
