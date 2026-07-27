package com.iemr.flw.masterEnum;

public enum DiagnosticOrderType {
    XRAY_CHEST(1),
    MTB(2),
    MTB_PLUS(3),
    MDR_RIF(4);

    private final int id;

    DiagnosticOrderType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return this.name();
    }

    public static DiagnosticOrderType fromId(int id) {
        for (DiagnosticOrderType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DiagnosticOrderType ID: " + id);
    }

    public static DiagnosticOrderType fromCode(String code) {
        for (DiagnosticOrderType type : values()) {
            if (type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DiagnosticOrderType code: " + code);
    }
}
