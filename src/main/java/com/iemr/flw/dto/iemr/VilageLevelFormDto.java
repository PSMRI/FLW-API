package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class VilageLevelFormDto {
    private Integer userId;
    private List<VilageLevelFormListDto> vilageLevelFormList;
}

