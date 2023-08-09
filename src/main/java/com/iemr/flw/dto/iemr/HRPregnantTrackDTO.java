package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HRPregnantTrackDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private Timestamp visitDate;

    private String rdPmsa;

    private String severeAnemia;

    private String pregInducedHypertension;

    private String gestDiabetesMellitus;

    private String hypothyrodism;

    private String polyhydromnios;

    private String oligohydromnios;

    private String antepartumHem;

    private String malPresentation;

    private String hivsyph;

    private String visit;
}

