package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HbncRequestDTO {

    private Long id;
    private Long benId;
    private Long hhId;
    private LocalDate homeVisitDate;
    private HbncVisitDTO hbncVisitDTO;
}
