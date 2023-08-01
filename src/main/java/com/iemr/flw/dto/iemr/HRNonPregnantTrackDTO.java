package com.iemr.flw.dto.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class HRNonPregnantTrackDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private Timestamp visitDate;

    private String anemia;

    private String hypertension;

    private String diabetes;

    private String severeAnemia;

    private String fp;

    private Timestamp lmp;

    private String missedPeriod;

    private String isPregnant;
}
