package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class HbncRequestDTO {

    private Long id;
    private Long benId;
    private Long hhId;
    private Integer homeVisitDate;

    private HbncVisitCardDTO hbncVisitCardDTO;

    private HbncPart1DTO hbncPart1DTO;

    private HbncPart2DTO hbncPart2DTO;

    private HbncVisitDTO hbncVisitDTO;
}
