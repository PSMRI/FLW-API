package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HbycDTO {

    private Long id;

    private Long benId;

    private String subcenterName;

    private String primaryHealthCenterName;

    private Integer villagePopulation;

    private Integer infantPopulation;

    private Timestamp visitDate;

    private String hbycAgeCategory;

    private Boolean orsPacketDelivered;

    private Boolean ironFolicAcidGiven;

    private Boolean isVaccinatedByAge;

    private Boolean wasIll;

    private Boolean referred;

    private Boolean supplementsGiven;

    private Boolean byHeightLength;

    private Boolean childrenWeighingLessReferred;

    private Boolean weightAccordingToAge;

    private Boolean delayInDevelopment;

    private Boolean referredToHealthInstitite;

    private Boolean vitaminASupplementsGiven;

    private String deathAge;

    private String deathCause;

    private Boolean qmOrAnmInformed;

    private String deathPlace;

    private Boolean superVisorOn;

    private Boolean orsShortage;

    private Boolean ifaDecreased;

    private String createdBy;

    private Timestamp createdDate;

    private Timestamp updatedDate;

    private String updatedBy;
}
