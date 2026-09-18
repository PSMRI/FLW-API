package com.iemr.flw.masterEnum;

/**
 * Registered diagnostic device integration providers. Add a new constant here when wiring up a
 * new DiagnosticProvider implementation (see DiagnosticProviderFactory) - diagnostic.provider.xray
 * and diagnostic.provider.truenat in application properties must each be set to one of these names.
 */
public enum DiagnosticProviderCode {
    EMRLITE
}