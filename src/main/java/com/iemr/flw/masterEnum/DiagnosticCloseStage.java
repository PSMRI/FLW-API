package com.iemr.flw.masterEnum;

/**
 * Which test a close reason is for, and so which reason_to_close_* column it lands in. A TrueNat
 * (MTB / MTB_PLUS) order carries two tests: MTB itself, then MDR RIF after a positive MTB.
 */
public enum DiagnosticCloseStage {
    XRAY,   // reason_to_close_xray   — X-ray not done
    MTB,    // reason_to_close_mtb    — MTB test not done
    RIF     // reason_to_close_mdrrif — MTB positive, MDR RIF test not done
}
