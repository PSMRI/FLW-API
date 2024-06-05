package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HbncVisitCardDTO {

    private Long id;
    private Long benId;
    private Integer visitNo;
    private String ashaName;
    private String villageName;
    private String subCenterName;
    private String blockName;
    private String motherName;
    private String fatherName;
    private Timestamp dateOfDelivery;
    private String placeOfDelivery;
    private String babyGender;
    private String typeOfDelivery;
    private Boolean stillBirth;
    private String startedBreastFeeding;
    private Timestamp dischargeDateMother;
    private Timestamp dischargeDateBaby;
    private Integer weightInGrams;
    private Boolean registrationOfBirth;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;
}
