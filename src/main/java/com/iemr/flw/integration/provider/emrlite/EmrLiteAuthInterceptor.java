package com.iemr.flw.integration.provider.emrlite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Attaches the current EMR Lite bearer token to every outgoing request and, on a 401,
 * forces a token refresh and retries once. Scoped to the emrLiteRestTemplate bean only —
 * must not be reused for other providers' RestTemplate instances.
 */
public class EmrLiteAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(EmrLiteAuthInterceptor.class);

    private final EmrLiteTokenManager tokenManager;

    public EmrLiteAuthInterceptor(EmrLiteTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().setBearerAuth(getValidToken());
        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            logger.warn("Received 401 from EMR Lite, foremrLiteRestTemplatecing token refresh and retrying");
            response.close();
            request.getHeaders().setBearerAuth(forceRefresh());
            return execution.execute(request, body);
        }
        return response;
    }

    private String getValidToken() throws IOException {
        try {
            return tokenManager.getValidToken();
        } catch (Exception e) {
            // Logged here (with full stack trace) because every current caller up the chain
            // (DiagnosticOrderServiceImpl, DiagnosticPollSchedulerService) only logs e.getMessage(),
            // which would otherwise show just this method's generic wrapper text. Folding the real
            // cause into the message too means those shallow logs become useful without having to
            // touch every call site.
            logger.error("Unable to obtain EMR Lite token", e);
            throw new IOException("Unable to obtain EMR Lite token: " + e.getMessage(), e);
        }
    }

    private String forceRefresh() throws IOException {
        try {
            return tokenManager.forceRefresh();
        } catch (Exception e) {
            logger.error("Unable to refresh EMR Lite token", e);
            throw new IOException("Unable to refresh EMR Lite token: " + e.getMessage(), e);
        }
    }
}
