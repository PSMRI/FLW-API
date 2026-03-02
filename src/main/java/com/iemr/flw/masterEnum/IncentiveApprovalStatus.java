package com.iemr.flw.masterEnum;

public enum IncentiveApprovalStatus {

    VERIFIED(101, "Verified"),
    PENDING(102, "Pending"),
    REJECTED(103, "Rejected"),
    OVERDUE(104, "Overdue");

    private final int code;
    private final String label;

    IncentiveApprovalStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    // 🔥 Reverse lookup (code → enum)
    public static IncentiveApprovalStatus fromCode(int code) {
        for (IncentiveApprovalStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid approval status code: " + code);
    }
}