package com.iemr.flw.integration.provider;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.masterEnum.DiagnosticOrderType;

public interface DiagnosticProvider {

    String getProviderCode();

    DiagnosticPushResult pushOrder(DiagnosticOrder order) throws Exception;

    DiagnosticPollResult pollResult(DiagnosticOrder order, boolean includeAssets) throws Exception;

    /**
     * Notifies the vendor that this order is being closed out without (or regardless of) a result,
     * so it stops tracking/processing it on their end. Only call for an order actually known to the
     * vendor (i.e. previously pushed) — calling this for an order that was never pushed is meaningless.
     * The raw response is returned (on both success and a provider-level rejection) so the caller can
     * persist it; only a transport-level failure (unreachable, timeout) throws instead, since there's
     * no response body to return in that case.
     */
    DiagnosticCancelResult cancelOrder(DiagnosticOrder order, String reason) throws Exception;

    /**
     * Lightweight liveness check against the vendor group serving this orderType. Never throws —
     * returns false on any failure (unreachable, non-2xx, unparsable response, unconfigured URL).
     */
    boolean checkHealth(DiagnosticOrderType orderType);
}
