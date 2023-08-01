package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HRNonPregnantAssessDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private String noOfDeliveries;

    private String timeLessThan18m;

    private String heightShort;

    private String age;

    private String misCarriage;

    private String homeDelivery;

    private String medicalIssues;

    private String pastCSection;

    private Boolean isHighRisk;

    private Timestamp visitDate;

}
