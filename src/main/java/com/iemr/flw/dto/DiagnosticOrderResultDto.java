package com.iemr.flw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DiagnosticOrderResultDto {

    private Long beneficiaryId;
    private Long visitCode;
    private String externalOrderId;
    private String orderType;
    private String status;
    private String errorMessage;
    private String reasonToCloseXray;
    private String reasonToCloseMTB;
    private String reasonToCloseMDRRIF;
    // One entry per tb_diagnostic_result row: the order's own test first, then MDR_RIF when present.
    private List<DiagnosticResultItemDto> results = new ArrayList<>();
}