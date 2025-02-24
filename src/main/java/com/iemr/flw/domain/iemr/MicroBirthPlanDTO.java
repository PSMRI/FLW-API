package com.iemr.flw.domain.iemr;

import lombok.Data;

import java.util.List;
@Data
public class MicroBirthPlanDTO {
    Integer  userId;
    List<MicroBirthPlan> entries;
}
