package com.iemr.flw.integration.provider.emrlite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Dedicated RestTemplate for EMR Lite diagnostic provider calls only.
 * Do NOT reuse this bean for other integrations (TM API, FHIR, etc.) — EmrLiteAuthInterceptor
 * unconditionally attaches an EMR Lite bearer token to every request that goes through it.
 */
@Configuration
public class EmrLiteRestTemplateConfig {

    @Bean
    public RestTemplate emrLiteRestTemplate(EmrLiteTokenManager emrLiteTokenManager) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new EmrLiteAuthInterceptor(emrLiteTokenManager));
        return restTemplate;
    }
}
