package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TBScreeningDTO {

    private Long id;
    private Long benId;
    private Long benRegId;
    private Long visitCode;

    private Timestamp visitDate;

    private Boolean coughMoreThan2Weeks;
    private Boolean bloodInSputum;
    private Boolean feverMoreThan2Weeks;
    private Boolean lossOfWeight;
    private Boolean nightSweats;
    private Boolean historyOfTb;
    private Boolean takingAntiTBDrugs;
    private Boolean familySufferingFromTB;
    private Boolean riseOfFever;
    private Boolean lossOfAppetite;
    private Boolean age;
    private Boolean diabetic;
    private Boolean tobaccoUser;
    private Boolean bmi;
    private Boolean contactWithTBPatient;
    private Boolean historyOfTBInLastFiveYrs;

    // Additional screening fields
    private String riskFactor;
    private Boolean fatigue;
    private Boolean chestPain;
    private Boolean shortBreath;

    // Screening option IDs
    private Integer bloodCheckId;
    private Integer coughCheckId;
    private Integer familyCheckId;
    private Integer feverCheckId;
    private Integer historyCheckId;
    private Integer weightCheckId;
    private Integer sweatsCheckId;
    private Integer drugsCheckId;
    private Integer lossOfAppetiteId;
    private Integer riseOfFeverId;

    private String sympotomatic;
    private String asymptomatic;
    private String recommandateTest;

    // Recommended tests and referrals
    private String recommendedForLiquidCulture;
    private Integer recommendedForLiquidCultureId;

    private String recommendedForTruenat;
    private Integer recommendedForTruenatId;

    private String referredForDigitalChestXray;
    private Integer referredForDigitalChestXrayId;

    private String referredForSputumCollection;
    private Integer referredForSputumCollectionId;

    private String sputumSampleSubmittedAt;
    private String testDenialReasons;

    // Audit fields
    private String createdBy;
    private Timestamp createdDate;
    private Boolean deleted;
    private Timestamp lastModDate;
    private String modifiedBy;
    private String processed;

    // Service and location details
    private Integer parkingPlaceID;
    private Integer providerServiceMapId;
    private Integer vanID;
    private Long vanSerialNo;
    private Integer hivStatusId;
    private String hivStatus;
    private List<Integer> keyPopulationRiskFactorIds;
    private List<String> keyPopulationRiskFactors;

}