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

    private String rdDengue;

    private String rdFilaria;

    private String severeAnemia;

    private String hemoglobinTest;

    private String ifaGiven;

    private Integer ifaQuantity;

    private String pregInducedHypertension;


    private Integer systolic;

    private Integer diastolic;

    private String gestDiabetesMellitus;

    private String bloodGlucoseTest;

    private Integer fbg;

    private Integer rbg;

    private Integer ppbg;
    
    private Integer fastingOgtt;

    private Integer after2hrsOgtt;

    private String hypothyrodism;

    private String polyhydromnios;

    private String oligohydromnios;

    private String antepartumHem;

    private String malPresentation;

    private String hivsyph;

    private String visit;
}

