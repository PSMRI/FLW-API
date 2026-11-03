package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class MicroBirthPlanDTO implements Serializable {
    private String syncedBy;
    private java.sql.Timestamp syncedDate;

    Integer  userId;
    List<MicroBirthPlan> entries;


}