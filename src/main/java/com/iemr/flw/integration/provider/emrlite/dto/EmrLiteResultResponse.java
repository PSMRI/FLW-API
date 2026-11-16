package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// XRAY_CHEST carries its summary/assets in the top-level "result"; TrueNat orders (MTB) carry them
// per component instead ("mtb", and "mdr_rif" once a positive MTB triggers it), with no top-level result.
@Data
@NoArgsConstructor
public class EmrLiteResultResponse {

    private String externalOrderId;
    private String orderType;
    private String status;
    private Map<String, Component> components;
    private ResultDto result;

    @Data
    @NoArgsConstructor
    public static class Component {
        private String status;
        private String provider;
        private String summary;
        private ComponentResultDto result;
    }

    @Data
    @NoArgsConstructor
    public static class ResultDto {
        private String summary;
        private Object rawJson;
        private String reportedAt;
        private List<AssetDto> assets;
    }

    @Data
    @NoArgsConstructor
    public static class ComponentResultDto {
        private Object rawJson;
        private String reportedAt;
        private List<AssetDto> assets;
    }

    @Data
    @NoArgsConstructor
    public static class AssetDto {
        private String type;
        private String contentType;
        private String fileName;
        private String base64;
    }
}
