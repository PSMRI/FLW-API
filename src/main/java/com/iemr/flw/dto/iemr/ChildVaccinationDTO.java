package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildVaccinationDTO {

    private long id;
    private Long beneficiaryRegId;
//    private String defaultReceivingAge;
    private String vaccineName;
    private Timestamp receivedDate;
    private String vaccinationreceivedat;
    private String vaccinatedBy;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp lastModDate;
    private Long beneficiaryId;
}
