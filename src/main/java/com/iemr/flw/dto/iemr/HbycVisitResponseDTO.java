package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Map;

@Data
public class HbycVisitResponseDTO {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private Map<String, Object> fields; // for dynamic form fields
}