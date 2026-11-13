package com.iemr.flw.masterEnum;

// XRAY_CHEST orders can carry up to three distinct artifacts (raw DICOM capture, AI heatmap,
// CAD report), and an MTB order up to two (the MTB report, plus the MDR RIF report when MTB is
// positive) — each needing its own value under the (externalOrderId, documentType) storage key.
public enum DiagnosticDocumentType {
    XRAY_CHEST,
    XRAY_CHEST_ANNOTATED,
    CAD,
    MTB_REPORT,
    MTB_PLUS_REPORT,
    MDR_RIF_REPORT;

    private static final String REPORT_ASSET_TYPE = "REPORT";
    private static final String HEATMAP_ASSET_TYPE = "HEATMAP";
    private static final String SECONDARY_CAPTURE_ASSET_TYPE = "SECONDARY_CAPTURE";
    private static final String MDR_RIF_COMPONENT = "mdr_rif";

    // component = which test on the order produced the file (see DiagnosticDocumentAsset); only
    // TrueNat orders use it, to tell the MDR RIF report apart from the MTB one.
    public static DiagnosticDocumentType from(String orderType, String assetType, String component) {
        DiagnosticOrderType type = DiagnosticOrderType.fromCode(orderType);
        if (type == DiagnosticOrderType.XRAY_CHEST) {
            if (REPORT_ASSET_TYPE.equalsIgnoreCase(assetType)) {
                return CAD;
            }
            if (HEATMAP_ASSET_TYPE.equalsIgnoreCase(assetType)
                    || SECONDARY_CAPTURE_ASSET_TYPE.equalsIgnoreCase(assetType)) {
                return XRAY_CHEST_ANNOTATED;
            }
            return XRAY_CHEST; // PRIMARY_CAPTURE — the DICOM image
        }
        if (MDR_RIF_COMPONENT.equalsIgnoreCase(component)) {
            return MDR_RIF_REPORT;
        }
        switch (type) {
            case MTB: return MTB_REPORT;
            case MTB_PLUS: return MTB_PLUS_REPORT;
            default: throw new IllegalArgumentException("Unknown orderType for document classification: " + orderType);
        }
    }

    public DiagnosticOrderType impliedOrderType() {
        switch (this) {
            case XRAY_CHEST:
            case XRAY_CHEST_ANNOTATED:
            case CAD:
                return DiagnosticOrderType.XRAY_CHEST;
            case MTB_REPORT:
            case MDR_RIF_REPORT: // MDR RIF runs on the MTB order itself — there is no separate MDR_RIF order
                return DiagnosticOrderType.MTB;
            case MTB_PLUS_REPORT:
                return DiagnosticOrderType.MTB_PLUS;
            default:
                throw new IllegalArgumentException("Unknown orderType for document type: " + this);
        }
    }
}
