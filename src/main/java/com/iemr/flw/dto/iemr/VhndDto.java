package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class VhndDto {
    private Integer userId;
    private List<VHNDFormDTO> entires;
}
