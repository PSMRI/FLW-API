package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class MaaMeetingListResponseDTO {
    private List<MaaMeetingResponseDTO> data;
    private Integer statusCode;
    private String status;
}
