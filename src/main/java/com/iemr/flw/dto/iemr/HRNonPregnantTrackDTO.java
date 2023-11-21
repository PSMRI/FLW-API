package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HRNonPregnantTrackDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private Timestamp visitDate;

    private String anemia;

    private String hypertension;

    private Integer systolic;

    private Integer diastolic;

    private String diabetes;

    private String bloodGlucoseTest;

    private Integer fbg;

    private Integer rbg;

    private Integer ppbg;

    private String severeAnemia;

    private String hemoglobinTest;

    private String ifaGiven;

    private Integer ifaQuantity;

    private String fp;

    private Timestamp lmp;

    private String missedPeriod;

    private String isPregnant;
}
