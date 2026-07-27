package com.iemr.flw.integration.provider;

import com.iemr.flw.domain.iemr.DiagnosticOrder;

public interface DiagnosticProvider {

    String getProviderCode();

    DiagnosticPushResult pushOrder(DiagnosticOrder order) throws Exception;

    DiagnosticPollResult pollResult(DiagnosticOrder order, boolean includeAssets) throws Exception;
}
