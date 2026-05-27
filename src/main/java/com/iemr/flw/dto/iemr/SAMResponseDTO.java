package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.SamVisitResponseDTO;
import lombok.Data;

import java.util.Map;

@Data
public class SAMResponseDTO {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private SamVisitResponseDTO fields; // for dynamic form fields
}

