package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class AncCounsellingCareDTO {
    private String formId;
    private Long id ;
    private Long beneficiaryId;
    private String visitDate;
    private AncCounsellingCareListDTO fields;

}



