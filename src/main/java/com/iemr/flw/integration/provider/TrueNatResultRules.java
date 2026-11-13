package com.iemr.flw.integration.provider;

import com.iemr.flw.masterEnum.DiagnosticOrderType;

/**
 * How a TrueNat (MTB / MTB_PLUS) order's two results are read, shared by the vendor poll and
 * manual entry so both store the same thing. One order carries both tests: the MTB result itself,
 * and an MDR RIF result that is only run when MTB comes back positive.
 */
public final class TrueNatResultRules {

    public static final String SUMMARY_TB_POSITIVE = "TB Positive";
    public static final String SUMMARY_TB_NEGATIVE = "TB Negative";
    public static final String SUMMARY_DR_TB = "DR TB";
    public static final String SUMMARY_NON_DR_TB = "Non DR TB";

    private TrueNatResultRules() {
    }

    public static boolean isTrueNat(String orderType) {
        return DiagnosticOrderType.MTB.name().equals(orderType) || DiagnosticOrderType.MTB_PLUS.name().equals(orderType);
    }

    /** A positive MTB result is followed by an MDR RIF test on the same order. */
    public static boolean needsMdrRif(String mtbSummary) {
        return mtbSummary != null && SUMMARY_TB_POSITIVE.equalsIgnoreCase(mtbSummary.trim());
    }

    /** TB presence from the MTB result; null for non-TrueNat orders or an unknown/error summary. */
    public static Boolean classifyTbPresence(String orderType, String summary) {
        if (!isTrueNat(orderType) || summary == null) {
            return null;
        }
        if (SUMMARY_TB_POSITIVE.equalsIgnoreCase(summary.trim())) {
            return true;
        }
        if (SUMMARY_TB_NEGATIVE.equalsIgnoreCase(summary.trim())) {
            return false;
        }
        return null;
    }

    /** Rifampicin resistance from the MDR RIF result; null when there is none or it is indeterminate. */
    public static Boolean classifyDrugResistance(String summary) {
        if (summary == null) {
            return null;
        }
        if (SUMMARY_DR_TB.equalsIgnoreCase(summary.trim())) {
            return true;
        }
        if (SUMMARY_NON_DR_TB.equalsIgnoreCase(summary.trim())) {
            return false;
        }
        return null;
    }
}
