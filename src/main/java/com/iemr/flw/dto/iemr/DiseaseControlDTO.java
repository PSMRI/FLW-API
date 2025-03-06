package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DiseaseControlDTO {
    private Integer id;
    private Long benId;
    private Timestamp caseDate;

    private Integer caseStatus;

    private List<String> symptoms;

    private int malariaCaseCount;

    private Integer referredTo;

    private String otherReferredTo;

    private Timestamp malariaCaseStatusDate;

    private String remarks;

    private Integer followUpPoint;

    private Timestamp followUpDate;



}

