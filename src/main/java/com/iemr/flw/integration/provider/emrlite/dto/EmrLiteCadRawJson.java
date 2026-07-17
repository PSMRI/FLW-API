package com.iemr.flw.integration.provider.emrlite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Typed view over EmrLiteResultResponse.ResultDto.rawJson, used only to pull the CAD
 * (e.g. DRONGOAI) findings list back out of that otherwise-opaque blob. Only the fields
 * needed for that extraction are declared - unmapped JSON fields (image, metadata, ...)
 * are ignored by Gson rather than modeled here.
 */
@Data
@NoArgsConstructor
public class EmrLiteCadRawJson {

    private ResultsDto results;

    @Data
    @NoArgsConstructor
    public static class ResultsDto {
        private List<FindingDto> findings;
    }

    @Data
    @NoArgsConstructor
    public static class FindingDto {
        private String name;
        private boolean presence;
        private double confidence;
    }
}