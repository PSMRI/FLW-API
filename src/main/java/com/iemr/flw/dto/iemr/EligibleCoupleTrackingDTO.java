package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class EligibleCoupleTrackingDTO implements Serializable {

    private Long id;

    private Long ecrId;

    private Timestamp visitDate;

    private String isPregnancyTestDone;

    private String pregnancyTestResult;

    private String isPregnant;

    private Boolean usingFamilyPlanning;

    private String methodOfContraception;

    private Timestamp createdDate;

    private String createdBy;
}
