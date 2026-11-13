package com.iemr.flw.masterEnum;

public enum DiagnosticOrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CLOSED,
    MANUAL_ENTRY,
    // MTB/RIF test came back invalid (e.g. "Error-2"); the TrueNat machine retests the same order by
    // itself, so the order is not closed — it keeps being polled until the retest result arrives.
    INVALID;

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
