package com.iemr.flw.integration.provider.emrlite;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.DiagnosticProvider;
import com.iemr.flw.integration.provider.DiagnosticPushResult;
import com.iemr.flw.integration.provider.emrlite.dto.*;
import com.iemr.flw.masterEnum.DiagnosticOrderStatus;
import com.iemr.flw.masterEnum.DiagnosticOrderType;
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

    private static final String SUMMARY_TB_POSITIVE = "TB Positive";
    private static final String SUMMARY_TB_NEGATIVE = "TB Negative";
    private static final String SUMMARY_DR_TB = "DR TB";
    private static final String SUMMARY_NON_DR_TB = "Non DR TB";

    @Value("${diagnostic.emrlite.order-url}")
    private String orderUrl;

    @Value("${diagnostic.emrlite.result-url}")
    private String resultUrl;

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
            responseBody = doPost(orderUrl, gson.toJson(request));
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            String providerMessage = null;
            String errorDetail = "";
            try {
                EmrLiteProviderResponse errorEnvelope = gson.fromJson(body, EmrLiteProviderResponse.class);
                if (errorEnvelope != null) {
                    providerMessage = errorEnvelope.getMessage();
                    errorDetail = describeErrorData(errorEnvelope.getData());
                }
            } catch (Exception parseEx) {
                logger.debug("Could not parse provider error body as envelope JSON for externalOrderId={}",
                        order.getExternalOrderId());
            }
            String errorMessage = (providerMessage != null && !providerMessage.isBlank())
                    ? "HTTP " + e.getStatusCode() + ": " + providerMessage + errorDetail
                    : "HTTP " + e.getStatusCode() + errorDetail;
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
        return new DiagnosticPushResult(true, orderResponse.getExternalOrderId(), responseBody, null);
    }

    @Override
    public DiagnosticPollResult pollResult(DiagnosticOrder order, boolean includeAssets) throws Exception {
        EmrLiteResultRequest request = new EmrLiteResultRequest(order.getExternalOrderId(), includeAssets);
        String responseBody;
        try {
            responseBody = doPost(resultUrl, gson.toJson(request));
        } catch (HttpClientErrorException.NotFound e) {
            // Permanent per the API contract — return FAILED instead of throwing, so the scheduler
            // stops instead of retrying on the transient back-off schedule.
            logger.warn("Provider returned 404 Not Found for externalOrderId={}", order.getExternalOrderId());
            return new DiagnosticPollResult(DiagnosticOrderStatus.FAILED, null, null,
                    e.getResponseBodyAsString(), null,
                    "Order not found on provider (404): externalOrderId=" + order.getExternalOrderId(),
                    false, null, false);
        }

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
        if (tbPresence == null) {
            tbPresence = classifyTbPresenceFromSummary(order.getOrderType(), summary);
        }
        Boolean drugResistancePresence = classifyDrugResistanceFromSummary(order.getOrderType(), summary);

        return new DiagnosticPollResult(status, order.getExternalOrderId(), summary, responseBody, assets, null,
                tbPresence, tbConfidence, drugResistancePresence);
    }

    private Boolean classifyTbPresenceFromSummary(String orderType, String summary) {
        if (!DiagnosticOrderType.MTB.name().equals(orderType) && !DiagnosticOrderType.MTB_PLUS.name().equals(orderType)) {
            return null; // TB presence isn't tested for this orderType — not applicable, not a confirmed negative
        }
        if (SUMMARY_TB_POSITIVE.equalsIgnoreCase(summary)) {
            return true;
        }
        if (SUMMARY_TB_NEGATIVE.equalsIgnoreCase(summary)) {
            return false;
        }
        return null; // Error/Indeterminate/anything else — genuinely unknown, not a confirmed negative
    }

    // MDR_RIF's summary reports rifampicin drug-resistance, not raw TB presence — kept as its own
    // field rather than folded into tbPresence.
    private Boolean classifyDrugResistanceFromSummary(String orderType, String summary) {
        if (!DiagnosticOrderType.MDR_RIF.name().equals(orderType)) {
            return null; // Drug resistance isn't tested for this orderType — not applicable, not a confirmed negative
        }
        if (SUMMARY_DR_TB.equalsIgnoreCase(summary)) {
            return true;
        }
        if (SUMMARY_NON_DR_TB.equalsIgnoreCase(summary)) {
            return false;
        }
        return null; // Error/Indeterminate/anything else — genuinely unknown, not a confirmed negative
    }

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

    // Provider's top-level "status" is unreliable on its own (can report COMPLETED while a
    // component is still PENDING), so true completion is derived from the components map instead.
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
            // Components contradict a terminal top-level status — only the components map may
            // resolve to a terminal state; treat this as still in progress.
            return DiagnosticOrderStatus.IN_PROGRESS;
        }
        return topLevelStatus;
    }

    private String describeErrorData(JsonElement data) {
        if (data == null || !data.isJsonObject()) {
            return "";
        }
        JsonObject dataObject = data.getAsJsonObject();
        JsonElement code = dataObject.get("code");
        JsonElement field = dataObject.get("field");
        if (code == null && field == null) {
            return "";
        }
        StringBuilder detail = new StringBuilder(" (");
        if (code != null) {
            detail.append("code=").append(code.getAsString());
        }
        if (field != null) {
            if (code != null) {
                detail.append(", ");
            }
            detail.append("field=").append(field.getAsString());
        }
        return detail.append(")").toString();
    }

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
