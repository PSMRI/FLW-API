package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBScreeningDTO {

    private Long id;

    private Long benId;

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
    private String sympotomatic;
    private String asymptomatic;
    private String recommandateTest;
}
