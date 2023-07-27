package com.iemr.flw.dto.identity;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class BeneficiaryRequestDTO {

    private Long userId;

    private Timestamp fromDate;

    private Timestamp toDate;

    private Integer pageNo;

}
