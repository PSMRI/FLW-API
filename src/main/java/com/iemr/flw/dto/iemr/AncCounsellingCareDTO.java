package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class AncCounsellingCareDTO {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    private String formId;
    private Long id ;
    private Long beneficiaryId;
    private String visitDate;
    private AncCounsellingCareListDTO fields;

}



