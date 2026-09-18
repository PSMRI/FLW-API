package com.iemr.flw.integration.provider;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.masterEnum.DiagnosticOrderType;

public interface DiagnosticProvider {

    String getProviderCode();

    DiagnosticPushResult pushOrder(DiagnosticOrder order) throws Exception;

    DiagnosticPollResult pollResult(DiagnosticOrder order, boolean includeAssets) throws Exception;

    /**
     * Lightweight liveness check against the vendor group serving this orderType. Never throws —
     * returns false on any failure (unreachable, non-2xx, unparsable response, unconfigured URL).
     */
    boolean checkHealth(DiagnosticOrderType orderType);
}
