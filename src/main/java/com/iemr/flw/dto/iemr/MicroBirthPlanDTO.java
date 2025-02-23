package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import lombok.Data;

import java.util.List;
@Data
public class MicroBirthPlanDTO {
    Integer  userId;
    List<MicroBirthPlan> entries;
}