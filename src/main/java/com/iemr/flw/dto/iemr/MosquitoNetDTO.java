package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class MosquitoNetDTO {
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String  userName;
    private MosquitoNetListDTO fields;
}
