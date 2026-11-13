package com.iemr.flw.integration.provider.emrlite;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.integration.provider.DiagnosticCancelResult;
import com.iemr.flw.integration.provider.DiagnosticDocumentAsset;
import com.iemr.flw.integration.provider.DiagnosticPollResult;
import com.iemr.flw.integration.provider.TrueNatResultRules;
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

    private static final String SUMMARY_TB_PRESUMPTIVE = "TB Presumptive";
    private static final String MDR_RIF_COMPONENT = "mdr_rif";
    private static final String VENDOR_DICOM_CONTENT_TYPE = "dicom/dcm";
    private static final String DICOM_CONTENT_TYPE = "application/dicom";

    @Value("${diagnostic.emrlite.xray.order-url}")
    private String xrayOrderUrl;

    @Value("${diagnostic.emrlite.xray.result-url}")
    private String xrayResultUrl;

    @Value("${diagnostic.emrlite.truenat.order-url}")
    private String truenatOrderUrl;

    @Value("${diagnostic.emrlite.truenat.result-url}")
    private String truenatResultUrl;

    @Value("${diagnostic.emrlite.xray.ping-url}")
    private String xrayPingUrl;

    @Value("${diagnostic.emrlite.truenat.ping-url}")
    private String truenatPingUrl;

    @Value("${diagnostic.emrlite.xray.cancel-url}")
    private String xrayCancelUrl;

    @Value("${diagnostic.emrlite.truenat.cancel-url}")
    private String truenatCancelUrl;

    @Autowired
    @Qualifier("emrLiteRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("emrLitePingRestTemplate")
    private RestTemplate pingRestTemplate;

    private final Gson gson = new Gson();

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public DiagnosticPushResult pushOrder(DiagnosticOrder order) throws Exception {
        EmrLiteOrderRequest request = buildOrderRequest(order);
        String orderUrl = DiagnosticOrderType.XRAY_CHEST.name().equals(order.getOrderType())
                ? xrayOrderUrl : truenatOrderUrl;
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
        String resultUrl = DiagnosticOrderType.XRAY_CHEST.name().equals(order.getOrderType())
                ? xrayResultUrl : truenatResultUrl;
        String responseBody;
        try {
            responseBody = doPost(resultUrl, gson.toJson(request));
        } catch (HttpClientErrorException.NotFound e) {
            // Treated as transient (order not yet registered on the provider, or a momentary
            // blip) rather than permanent — throw so the order's status is left untouched and
            // the scheduler retries on the next tick instead of marking it FAILED.
            logger.warn("Provider returned 404 Not Found for externalOrderId={}", order.getExternalOrderId());
            throw new Exception("Order not found on provider (404): externalOrderId=" + order.getExternalOrderId(), e);
        }

        EmrLiteProviderResponse envelope = gson.fromJson(responseBody, EmrLiteProviderResponse.class);
        if (!envelope.isSuccess()) {
            throw new Exception("Provider rejected result poll: " + envelope.getMessage());
        }

        EmrLiteResultResponse result = gson.fromJson(envelope.getData(), EmrLiteResultResponse.class);
        boolean xray = DiagnosticOrderType.XRAY_CHEST.name().equals(order.getOrderType());

        // TrueNat orders report per component: the test itself under its own orderType key
        // ("mtb" / "mtb_plus"), plus "mdr_rif" which the vendor adds when MTB comes back positive.
        EmrLiteResultResponse.Component testComponent = xray ? null : component(result, order.getOrderType());
        EmrLiteResultResponse.Component mdrRif = xray ? null : component(result, MDR_RIF_COMPONENT);
        String summary = xray || testComponent == null
                ? (result.getResult() != null ? result.getResult().getSummary() : null)
                : testComponent.getSummary();
        String drugResistanceSummary = mdrRif != null ? mdrRif.getSummary() : null;

        DiagnosticOrderStatus status = deriveOrderStatus(result);
        if (!xray && awaitingMdrRif(summary, mdrRif)) {
            // A positive MTB triggers an MDR RIF test on the same order — keep the order PENDING so the
            // scheduler keeps polling until the RIF result is in, instead of completing on MTB alone.
            status = DiagnosticOrderStatus.PENDING;
        }
        List<DiagnosticDocumentAsset> assets = mapAssets(result);

        Boolean tbPresence;
        Double tbConfidence = null; // dropped for X-ray; MTB/MTB_PLUS never populated this either
        if (xray) {
            tbPresence = classifyXrayTbPresenceFromSummary(status, summary);
        } else {
            tbPresence = classifyTbPresenceFromSummary(order.getOrderType(), summary);
        }
        Boolean drugResistancePresence = classifyDrugResistanceFromSummary(drugResistanceSummary);

        DiagnosticPollResult pollResult = new DiagnosticPollResult(status, order.getExternalOrderId(), summary,
                responseBody, assets, null, tbPresence, tbConfidence, drugResistancePresence, drugResistanceSummary);
        pollResult.setDrugResistanceStatus(mdrRif != null && mdrRif.getStatus() != null
                ? mdrRif.getStatus().toUpperCase(java.util.Locale.ROOT) : null);
        return pollResult;
    }

    private EmrLiteResultResponse.Component component(EmrLiteResultResponse result, String key) {
        if (result.getComponents() == null) {
            return null;
        }
        return result.getComponents().entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    // True while a positive MTB's MDR RIF test hasn't reached a terminal state yet (not created by the
    // vendor yet, or still running). A FAILED RIF is terminal — deriveOrderStatus already reports it.
    private boolean awaitingMdrRif(String summary, EmrLiteResultResponse.Component mdrRif) {
        if (!TrueNatResultRules.needsMdrRif(summary)) {
            return false;
        }
        return mdrRif == null
                || !("COMPLETED".equalsIgnoreCase(mdrRif.getStatus()) || "FAILED".equalsIgnoreCase(mdrRif.getStatus()));
    }

    @Override
    public DiagnosticCancelResult cancelOrder(DiagnosticOrder order, String reason) throws Exception {
        EmrLiteCancelRequest request = new EmrLiteCancelRequest(order.getExternalOrderId(), reason);
        String cancelUrl = DiagnosticOrderType.XRAY_CHEST.name().equals(order.getOrderType())
                ? xrayCancelUrl : truenatCancelUrl;
        String responseBody;
        try {
            responseBody = doPost(cancelUrl, gson.toJson(request));
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            logger.warn("Provider HTTP error on order cancel: externalOrderId={}, status={}",
                    order.getExternalOrderId(), e.getStatusCode());
            return new DiagnosticCancelResult(false, body, "HTTP " + e.getStatusCode());
        }

        EmrLiteProviderResponse envelope = gson.fromJson(responseBody, EmrLiteProviderResponse.class);
        if (envelope == null || !envelope.isSuccess()) {
            logger.warn("Provider rejected order cancel: externalOrderId={}, message={}",
                    order.getExternalOrderId(), envelope != null ? envelope.getMessage() : null);
            return new DiagnosticCancelResult(false, responseBody,
                    envelope != null ? envelope.getMessage() : "empty response");
        }

        logger.info("Order cancelled on provider: externalOrderId={}, reason={}",
                order.getExternalOrderId(), reason);
        return new DiagnosticCancelResult(true, responseBody, null);
    }

    @Override
    public boolean checkHealth(DiagnosticOrderType orderType) {
        String pingUrl = DiagnosticOrderType.XRAY_CHEST.equals(orderType) ? xrayPingUrl : truenatPingUrl;
        if (pingUrl == null || pingUrl.isBlank()) {
            logger.warn("EMR Lite ping URL not configured for orderType={}", orderType);
            return false;
        }
        try {
            ResponseEntity<String> response = pingRestTemplate.getForEntity(pingUrl, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return false;
            }
            EmrLiteProviderResponse envelope = gson.fromJson(response.getBody(), EmrLiteProviderResponse.class);
            return envelope != null && envelope.isSuccess();
        } catch (Exception e) {
            logger.warn("EMR Lite ping failed: url={}, error={}", pingUrl, e.getMessage());
            return false;
        }
    }

    Boolean classifyTbPresenceFromSummary(String orderType, String summary) {
        return TrueNatResultRules.classifyTbPresence(orderType, summary);
    }

    // The mdr_rif component's summary reports rifampicin drug-resistance, not raw TB presence — kept
    // as its own field rather than folded into tbPresence. Null summary = no RIF test (MTB negative,
    // X-ray) or not finished yet.
    Boolean classifyDrugResistanceFromSummary(String summary) {
        return TrueNatResultRules.classifyDrugResistance(summary);
    }

    // X-ray summary carries no numeric score (tbConfidence is always null for this order type —
    // see pollResult). Only "TB Presumptive" is positive; every other non-null summary is negative.
    // A null summary (order not yet completed) stays null/unknown rather than a confirmed negative.
    Boolean classifyXrayTbPresenceFromSummary(DiagnosticOrderStatus status, String summary) {
        if (status != DiagnosticOrderStatus.COMPLETED || summary == null) {
            return null;
        }
        return SUMMARY_TB_PRESUMPTIVE.equalsIgnoreCase(summary);
    }

    // Provider's top-level "status" is unreliable on its own (can report COMPLETED while a
    // component is still PENDING), so true completion is derived from the components map instead.
    private DiagnosticOrderStatus deriveOrderStatus(EmrLiteResultResponse result) {
        Map<String, EmrLiteResultResponse.Component> components = result.getComponents();
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
            // resolve to a terminal state; treat this as still pending.
            return DiagnosticOrderStatus.PENDING;
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

    // X-ray files sit in the top-level result; TrueNat files sit in each component's result, so each
    // one is tagged with its component key ("mtb" / "mdr_rif") to keep the two reports apart.
    private List<DiagnosticDocumentAsset> mapAssets(EmrLiteResultResponse result) {
        List<DiagnosticDocumentAsset> assets = new ArrayList<>();
        if (result.getResult() != null) {
            addAssets(assets, result.getResult().getAssets(), null);
        }
        if (result.getComponents() != null) {
            result.getComponents().forEach((key, component) -> {
                if (component != null && component.getResult() != null) {
                    addAssets(assets, component.getResult().getAssets(), key);
                }
            });
        }
        return assets;
    }

    private void addAssets(List<DiagnosticDocumentAsset> assets, List<EmrLiteResultResponse.AssetDto> assetDtos,
                           String component) {
        if (assetDtos == null) {
            return;
        }
        for (EmrLiteResultResponse.AssetDto assetDto : assetDtos) {
            assets.add(new DiagnosticDocumentAsset(assetDto.getType(), toStandardContentType(assetDto.getContentType()),
                    assetDto.getFileName(), assetDto.getBase64(), component));
        }
    }

    // EMR Lite labels the X-ray capture "dicom/dcm", which isn't a registered media type — store and
    // serve it as the standard application/dicom instead.
    private String toStandardContentType(String contentType) {
        return VENDOR_DICOM_CONTENT_TYPE.equalsIgnoreCase(contentType) ? DICOM_CONTENT_TYPE : contentType;
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
                toProviderSex(order.getPatientSex()));
        String orderedAt = OffsetDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new EmrLiteOrderRequest(
                String.valueOf(order.getBeneficiaryId()),
                String.valueOf(order.getVisitCode()),
                order.getExternalOrderId(),
                order.getOrderType(),
                orderedAt,
                patient);
    }

    private String toProviderSex(String patientSex) {
        return "Others".equalsIgnoreCase(patientSex) ? "Others" : patientSex;
    }
}
