package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class VhncDto {
    private Integer userId;
    private List<VhncFormDTO> entries;
}