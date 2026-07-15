package com.iemr.flw.masterEnum;

/**
 * Classifies a stored DiagnosticDocument independently of the parent DiagnosticOrder's own
 * orderType (which stays e.g. XRAY_CHEST regardless of which artifact this is). Derived
 * automatically from the provider's asset.type at ingest time - callers never pass a raw
 * "CAD" string, they just ask for this enum.
 */
public enum DiagnosticDocumentType {
    XRAY_CHEST,
    CAD;

    private static final String CAD_REPORT_ASSET_TYPE = "REPORT";

    public static DiagnosticDocumentType fromAssetType(String assetType) {
        return CAD_REPORT_ASSET_TYPE.equalsIgnoreCase(assetType) ? CAD : XRAY_CHEST;
    }
}