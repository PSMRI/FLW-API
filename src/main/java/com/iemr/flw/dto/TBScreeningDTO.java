package com.iemr.flw.dto;

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
}
