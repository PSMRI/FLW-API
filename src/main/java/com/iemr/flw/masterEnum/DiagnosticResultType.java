package com.iemr.flw.masterEnum;

/**
 * Which test a tb_diagnostic_result row holds. An order has one row for its own test (named like its
 * orderType) and, for MTB / MTB_PLUS, a second MDR_RIF row once the RIF test exists (run after a
 * positive MTB on the same order).
 */
public enum DiagnosticResultType {
    XRAY_CHEST,
    MTB,
    MTB_PLUS,
    MDR_RIF
}
