package com.iemr.flw.masterEnum;

public enum GroupName {
    CHILD_HEALTH("CHILD HEALTH"),
    IMMUNIZATION("IMMUNIZATION"),
    MATERNAL_HEALTH("MATERNAL HEALTH"),
    JSY("JSY"),
    FAMILY_PLANNING("FAMILY PLANNING"),
    ADOLESCENT_HEALTH("ADOLESCENT HEALTH"),
    ASHA_MONTHLY_ROUTINE("ASHA MONTHLY ROUTINE"),
    UMBRELLA_PROGRAMMES("UMBRELLA PROGRAMMES"),
    NCD("NCD"),
    ADDITIONAL_INCENTIVE("ADDITIONAL INCENTIVE"),
    OTHER_INCENTIVES("OTHER INCENTIVES"),
    ACTIVITY("ACTIVITY");

    private final String originalDisplayName;
    private static boolean isCh = false;

    GroupName(String displayName) {
        this.originalDisplayName = displayName;
    }

    // Set the master flag
    public static void setIsCh(boolean flag) {
        isCh = flag;
    }

    // Get display name based on isCh
    public String getDisplayName() {
        return isCh ? "ACTIVITY" : originalDisplayName;
    }
}





