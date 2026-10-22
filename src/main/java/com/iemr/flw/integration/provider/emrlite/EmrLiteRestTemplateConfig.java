package com.iemr.flw.integration.provider.emrlite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Dedicated RestTemplate for EMR Lite diagnostic provider calls only.
 * Do NOT reuse this bean for other integrations (TM API, FHIR, etc.) — EmrLiteAuthInterceptor
 * unconditionally attaches an EMR Lite bearer token to every request that goes through it.
 */
@Configuration
public class EmrLiteRestTemplateConfig {

    private static final int VENDOR_CALL_TIMEOUT_MS = 20000;
    // Result responses carry document assets inline as base64, so reads get more headroom than connects.
    private static final int VENDOR_READ_TIMEOUT_MS = 60000;
    private static final int PING_TIMEOUT_MS = 5000;

    // Timeouts are required, not optional: order pushes run while holding the per-beneficiary+orderType
    // order lock (see DiagnosticOrderServiceImpl), so a hung vendor call without them would hold that lock forever.
    @Bean
    public RestTemplate emrLiteRestTemplate(EmrLiteTokenManager emrLiteTokenManager) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(VENDOR_CALL_TIMEOUT_MS);
        factory.setReadTimeout(VENDOR_READ_TIMEOUT_MS);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new EmrLiteAuthInterceptor(emrLiteTokenManager));
        return restTemplate;
    }

    // Unauthenticated: the vendor's ping/health endpoint needs no token, and must not go through
    // EmrLiteAuthInterceptor (which would force a login just to answer a liveness check).
    @Bean
    public RestTemplate emrLitePingRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(PING_TIMEOUT_MS);
        factory.setReadTimeout(PING_TIMEOUT_MS);
        return new RestTemplate(factory);
    }
}