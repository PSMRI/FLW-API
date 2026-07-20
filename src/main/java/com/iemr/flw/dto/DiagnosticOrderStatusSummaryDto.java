package com.iemr.flw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticOrderStatusSummaryDto {
    private List<Long> awaitingTestCompletion;
    private List<Long> awaitingProviderResult;
    private List<Long> completed;
}
