package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.Map;

@Data
public class AncCounsellingCareResponseDTO {
    private String formId;
    private Long beneficiaryId;
    private String visitDate;
    private Map<String, Object> fields; // for dynamic form fields

}



