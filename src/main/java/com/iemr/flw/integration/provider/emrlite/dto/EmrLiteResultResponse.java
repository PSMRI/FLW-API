package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class EmrLiteResultResponse {

    private String externalOrderId;
    private String orderType;
    private String status;
    private Map<String, ComponentStatus> components;
    private ResultDto result;

    @Data
    @NoArgsConstructor
    public static class ComponentStatus {
        private String status;
        private String provider;
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
    public static class AssetDto {
        private String type;
        private String contentType;
        private String fileName;
        private String base64;
    }
}
