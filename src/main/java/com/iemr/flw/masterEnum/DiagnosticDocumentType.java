package com.iemr.flw.masterEnum;

// XRAY_CHEST orders can carry up to three distinct artifacts (raw capture, AI-annotated capture,
// CAD report), each needing its own value under the (benRegID, documentType) storage key.
public enum DiagnosticDocumentType {
    XRAY_CHEST,
    XRAY_CHEST_ANNOTATED,
    CAD,
    MTB_REPORT,
    MTB_PLUS_REPORT,
    MDR_RIF_REPORT;

    private static final String REPORT_ASSET_TYPE = "REPORT";
    private static final String SECONDARY_CAPTURE_ASSET_TYPE = "SECONDARY_CAPTURE";

    public static DiagnosticDocumentType from(String orderType, String assetType) {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            if (REPORT_ASSET_TYPE.equalsIgnoreCase(assetType)) {
                return CAD;
            }
            if (SECONDARY_CAPTURE_ASSET_TYPE.equalsIgnoreCase(assetType)) {
                return XRAY_CHEST_ANNOTATED;
            }
            return XRAY_CHEST;
        }
        switch (type) {
            case MTB: return MTB_REPORT;
            case MTB_PLUS: return MTB_PLUS_REPORT;
            case MDR_RIF: return MDR_RIF_REPORT;
            default: throw new IllegalArgumentException("Unknown orderType for document classification: " + orderType);
        }
    }
}
