package com.iemr.flw.dto.iemr;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IFAFormSubmissionResponse {
    private Long beneficiaryId;
    private String visitDate;
    private Long houseHoldId;
    private String createdBy;
    private String formId;
    private String createdAt;
    private IFAFormFieldsDTO fields;
}
