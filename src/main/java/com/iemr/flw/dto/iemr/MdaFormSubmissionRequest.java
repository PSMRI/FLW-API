package com.iemr.flw.dto.iemr;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class MdaFormSubmissionRequest {
    private Long beneficiaryId;
    private String formId;
    private Long houseHoldId;
    private String userName;
    private String  visitDate;
    private MdaFormFieldsDTO fields;
    private Timestamp createdDate;
}
