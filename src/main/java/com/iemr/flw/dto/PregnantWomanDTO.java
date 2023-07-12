package com.iemr.flw.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PregnantWomanDTO {

    private Long id;

    private String benId;

    private Timestamp registrationDate;

    private Long rchId;

    private Long mcpCardId;

    private Timestamp lmpDate;

    private String bloodGroup;

    private Integer weight;

    private Integer height;

    private String rprTestResult;

    private Timestamp dateOfRprTest;

    private String hivTestResult;

    private String hbsAgTestResult;

    private Timestamp dateOfHivTest;

    private String pastIllness;

    private Boolean isFirstPregnancyTest;

    private Integer numPrevPregnancy;

    private String hadPregCompilation;

    private String pregCompilation;
}
