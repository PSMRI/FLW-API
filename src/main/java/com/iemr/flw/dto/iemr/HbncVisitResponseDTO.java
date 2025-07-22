package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.util.Date;
import java.util.Map;

@Data
public class HbncVisitResponseDTO {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private Map<String, Object> fields; // for dynamic form fields
}
