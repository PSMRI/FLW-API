package com.iemr.flw.integration.provider.emrlite;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.DiagnosticProvider;
import com.iemr.flw.integration.provider.DiagnosticPushResult;
import com.iemr.flw.integration.provider.emrlite.dto.*;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticProviderCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EmrLiteProvider implements DiagnosticProvider {

    private static final Logger logger = LoggerFactory.getLogger(EmrLiteProvider.class);
    private static final String PROVIDER_CODE = DiagnosticProviderCode.EMRLITE.name();
    private static final String CAD_COMPONENT_KEY = "cad";
    private static final String TUBERCULOSIS_FINDING_NAME = "Tuberculosis";

    @Value("${diagnostic.emrlite.order-url}")
    private String orderUrl;

    @Value("${diagnostic.emrlite.result-url}")
    private String resultUrl;

    // No longer used directly here — EmrLiteAuthInterceptor (wired into the emrLiteRestTemplate
    // bean via EmrLiteRestTemplateConfig) now owns calling tokenManager.getValidToken()/forceRefresh().
    // Kept commented for reference/rollback alongside postWithAuth(...) below.
    // @Autowired
    // private EmrLiteTokenManager tokenManager;

    // Superseded by the interceptor-backed emrLiteRestTemplate bean below (EmrLiteAuthInterceptor
    // now handles attaching/refreshing the bearer token for every call on this RestTemplate).
    // private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    @Qualifier("emrLiteRestTemplate")
    private RestTemplate restTemplate;

    private final Gson gson = new Gson();

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public DiagnosticPushResult pushOrder(DiagnosticOrder order) throws Exception {
        EmrLiteOrderRequest request = buildOrderRequest(order);
        String responseBody;
        try {
            // responseBody = postWithAuth(orderUrl, gson.toJson(request)); // superseded by EmrLiteAuthInterceptor
            responseBody = doPost(orderUrl, gson.toJson(request));
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            String providerMessage = null;
            try {
                EmrLiteProviderResponse errorEnvelope = gson.fromJson(body, EmrLiteProviderResponse.class);
                if (errorEnvelope != null) {
                    providerMessage = errorEnvelope.getMessage();
                }
            } catch (Exception parseEx) {
                logger.debug("Could not parse provider error body as envelope JSON for externalOrderId={}",
                        order.getExternalOrderId());
            }
            String errorMessage = (providerMessage != null && !providerMessage.isBlank())
                    ? "HTTP " + e.getStatusCode() + ": " + providerMessage
                    : "HTTP " + e.getStatusCode();
            logger.warn("Provider HTTP error on order push: externalOrderId={}, status={}, message={}",
                    order.getExternalOrderId(), e.getStatusCode(), providerMessage);
            return new DiagnosticPushResult(false, null, body, errorMessage);
        }

        EmrLiteProviderResponse envelope = gson.fromJson(responseBody, EmrLiteProviderResponse.class);
        if (!envelope.isSuccess()) {
            logger.warn("Provider rejected order push: externalOrderId={}, message={}",
                    order.getExternalOrderId(), envelope.getMessage());
            return new DiagnosticPushResult(false, null, responseBody, envelope.getMessage());
        }

        EmrLiteOrderResponse orderResponse = gson.fromJson(envelope.getData(), EmrLiteOrderResponse.class);
        logger.info("Order pushed: externalOrderId={}, status={}",
                order.getExternalOrderId(), orderResponse.getStatus());
        // Provider echoes our externalOrderId back; store it as providerOrderId for audit
        return new DiagnosticPushResult(true, orderResponse.getExternalOrderId(), responseBody, null);
    }

    @Override
    public DiagnosticPollResult pollResult(DiagnosticOrder order, boolean includeAssets) throws Exception {
        // Always poll using our own externalOrderId per the API contract
        EmrLiteResultRequest request = new EmrLiteResultRequest(order.getExternalOrderId(), includeAssets);
        String responseBody;
        try {
            // responseBody = postWithAuth(resultUrl, gson.toJson(request)); // superseded by EmrLiteAuthInterceptor
            responseBody = doPost(resultUrl, gson.toJson(request));
        } catch (HttpClientErrorException.NotFound e) {
            // Permanent per the API contract ("does not exist or belongs to a different integration
            // client") — return a normal FAILED result instead of throwing, so the poll scheduler
            // stops immediately instead of retrying this on the transient exponential back-off schedule.
            logger.warn("Provider returned 404 Not Found for externalOrderId={}", order.getExternalOrderId());
            return new DiagnosticPollResult(DiagnosticOrderStatus.FAILED, null, null,
                    e.getResponseBodyAsString(), null,
                    "Order not found on provider (404): externalOrderId=" + order.getExternalOrderId(),
                    null, null);
        }
        // Other HttpStatusCodeException subtypes (401 surviving the auth-interceptor retry, 5xx, etc.)
        // are not caught here and propagate to the scheduler's existing transient-retry-with-backoff path.

        EmrLiteProviderResponse envelope = gson.fromJson(responseBody, EmrLiteProviderResponse.class);
        if (!envelope.isSuccess()) {
            throw new Exception("Provider rejected result poll: " + envelope.getMessage());
        }

        EmrLiteResultResponse result = gson.fromJson(envelope.getData(), EmrLiteResultResponse.class);
        DiagnosticOrderStatus status = deriveOrderStatus(result);

        String summary = result.getResult() != null ? result.getResult().getSummary() : null;
        List<DiagnosticDocumentAsset> assets = mapAssets(result);
        EmrLiteCadRawJson.FindingDto tbFinding = extractTuberculosisFinding(result);
        Boolean tbPresence = tbFinding != null ? tbFinding.isPresence() : null;
        Double tbConfidence = tbFinding != null ? tbFinding.getConfidence() : null;

        return new DiagnosticPollResult(status, order.getExternalOrderId(), summary, responseBody, assets, null,
                tbPresence, tbConfidence);
    }

    // The CAD (e.g. DRONGOAI) findings list lives inside rawJson.results.findings, which only
    // exists once a "cad" component has run. Deliberately does not touch rawJson.results.image -
    // that's an internal provider media path, not a URL or base64 payload we can use; the actual
    // X-ray capture (when sent) arrives separately via result.assets, handled by mapAssets() above.
    private EmrLiteCadRawJson.FindingDto extractTuberculosisFinding(EmrLiteResultResponse result) {
        if (result.getComponents() == null || !result.getComponents().containsKey(CAD_COMPONENT_KEY)) {
            return null;
        }
        if (result.getResult() == null || result.getResult().getRawJson() == null) {
            return null;
        }
        try {
            EmrLiteCadRawJson cadRawJson =
                    gson.fromJson(gson.toJson(result.getResult().getRawJson()), EmrLiteCadRawJson.class);
            if (cadRawJson.getResults() == null || cadRawJson.getResults().getFindings() == null) {
                return null;
            }
            return cadRawJson.getResults().getFindings().stream()
                    .filter(f -> TUBERCULOSIS_FINDING_NAME.equalsIgnoreCase(f.getName()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            logger.warn("Failed to parse CAD rawJson findings for externalOrderId={}: {}",
                    result.getExternalOrderId(), e.getMessage());
            return null;
        }
    }

    // The provider's top-level "status" is not reliable on its own: a real example response
    // ("XRAY Completed with Assets, CAD Pending") shows status=COMPLETED while the cad component
    // is still PENDING and the summary literally says "CAD Pending". Derive true completion from
    // the components map instead - COMPLETED only when every component is COMPLETED, FAILED if any
    // component is FAILED - falling back to the top-level status only for the PENDING/IN_PROGRESS
    // distinction (or when the provider sends no components at all).
    private DiagnosticOrderStatus deriveOrderStatus(EmrLiteResultResponse result) {
        Map<String, EmrLiteResultResponse.ComponentStatus> components = result.getComponents();
        if (components == null || components.isEmpty()) {
            return DiagnosticOrderStatus.fromString(result.getStatus());
        }
        boolean anyFailed = components.values().stream()
                .anyMatch(c -> "FAILED".equalsIgnoreCase(c.getStatus()));
        if (anyFailed) {
            return DiagnosticOrderStatus.FAILED;
        }
        boolean allCompleted = components.values().stream()
                .allMatch(c -> "COMPLETED".equalsIgnoreCase(c.getStatus()));
        if (allCompleted) {
            return DiagnosticOrderStatus.COMPLETED;
        }
        DiagnosticOrderStatus topLevelStatus = DiagnosticOrderStatus.fromString(result.getStatus());
        if (topLevelStatus == DiagnosticOrderStatus.COMPLETED || topLevelStatus == DiagnosticOrderStatus.FAILED) {
            // Components contradict a terminal top-level status (e.g. xray COMPLETED, cad PENDING,
            // top-level status still says COMPLETED) — only the components map may resolve to a
            // terminal state; treat this as still in progress.
            return DiagnosticOrderStatus.IN_PROGRESS;
        }
        return topLevelStatus;
    }

    // Assets can be present before the order is fully complete (e.g. the X-ray's PRIMARY_CAPTURE
    // is returned while CAD is still PENDING), so this is extracted independently of order status.
    private List<DiagnosticDocumentAsset> mapAssets(EmrLiteResultResponse result) {
        if (result.getResult() == null || result.getResult().getAssets() == null) {
            return new ArrayList<>();
        }
        List<DiagnosticDocumentAsset> assets = new ArrayList<>();
        for (EmrLiteResultResponse.AssetDto assetDto : result.getResult().getAssets()) {
            assets.add(new DiagnosticDocumentAsset(
                    assetDto.getType(), assetDto.getContentType(), assetDto.getFileName(), assetDto.getBase64()));
        }
        return assets;
    }

    // Superseded by EmrLiteAuthInterceptor, which now attaches/refreshes the bearer token
    // for every call made on the emrLiteRestTemplate bean. Kept for reference/rollback.
    // private String postWithAuth(String url, String jsonBody) throws Exception {
    //     String token = tokenManager.getValidToken();
    //     try {
    //         return doPost(url, jsonBody, token);
    //     } catch (HttpClientErrorException.Unauthorized e) {
    //         logger.warn("Received 401 from provider, forcing token refresh and retrying");
    //         token = tokenManager.forceRefresh();
    //         return doPost(url, jsonBody, token);
    //     }
    // }
    //
    // private String doPost(String url, String jsonBody, String token) {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.setBearerAuth(token);
    //     HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
    //     ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    //     return response.getBody();
    // }

    private String doPost(String url, String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private EmrLiteOrderRequest buildOrderRequest(DiagnosticOrder order) {
        EmrLiteOrderRequest.PatientDto patient = new EmrLiteOrderRequest.PatientDto(
                order.getPatientFirstName(),
                order.getPatientLastName(),
                order.getPatientDateOfBirth(),
                order.getPatientSex());
        String orderedAt = OffsetDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new EmrLiteOrderRequest(
                String.valueOf(order.getBenRegID()),
                String.valueOf(order.getVisitCode()),
                order.getExternalOrderId(),
                order.getOrderType(),
                orderedAt,
                patient);
    }
}
