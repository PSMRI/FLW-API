package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.LeprosyData;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class LeprosyDTO {
    Integer userId;
    List<LeprosyData> leprosyLists;



}

