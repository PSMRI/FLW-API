package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class LeprosyDTO {
    Integer userId;
    List<DiseaseLeprosyDTO> leprosyLists;



}

