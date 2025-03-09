package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DiseaseControlDTO {
    private Integer id;
    private Long benId;
    private Timestamp caseDate;

    private Integer caseStatus;

    private String symptoms;

    private int malariaCaseCount;

    private Integer referredTo;

    private String otherReferredTo;

    private Timestamp malariaCaseStatusDate;

    private String remarks;

    private Integer followUpPoint;

    private Timestamp followUpDate;

    private String status;

    private String otherStatus;

    private String body_part;

    private Boolean sufferingFromFilariasis;

    private Timestamp homeVisitDate;

    private Timestamp LeprosyStatusDate;

    private String MedicineSideEffect;


}

