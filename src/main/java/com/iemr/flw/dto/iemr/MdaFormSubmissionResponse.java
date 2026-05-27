package com.iemr.flw.dto.iemr;

import java.sql.Timestamp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MdaFormSubmissionResponse {
    private Long beneficiaryId;
    private String visitDate;
    private Long houseHoldId;
    private Integer ashaId; 
    private String createdBy;
    private String formId;
    private Timestamp createdAt;
    private MdaFormFieldsDTO fields;

}
