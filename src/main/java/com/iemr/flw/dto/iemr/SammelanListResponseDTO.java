package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class SammelanListResponseDTO {
    private List<SammelanResponseDTO> data;
    private Integer statusCode;
    private String status;

}
