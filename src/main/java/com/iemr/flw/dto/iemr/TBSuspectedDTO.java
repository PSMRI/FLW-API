package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBSuspectedDTO {

    private Long id;

    private Long userId;

    private Long benId;

    private Timestamp visitDate;

    private Boolean isSputumCollected;

    private String sputumSubmittedAt;

    private String nikshayId;

    private String sputumTestResult;

    private Boolean referred;

    private String followUps;
}
