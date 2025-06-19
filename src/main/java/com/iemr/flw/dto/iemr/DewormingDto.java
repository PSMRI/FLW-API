package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class DewormingDto {
    private Integer userId;
    private List<DewormingFormDTO> entries;
}