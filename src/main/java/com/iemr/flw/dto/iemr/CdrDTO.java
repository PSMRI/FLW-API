package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
public class CdrDTO {
    private Long id;

    private Long benId;

    private String motherName;

    private String fatherName;

    private String visitDate;

    private String address;

    private String houseNo;

    private String colony;

    private Integer pincode;

    private String landmarks;

    private Long landline;

    private Long mobile;

    private String deathDate;

    private Timestamp placeOfDeath;

    private String nameOfInformant;

    private Time deathTime;

    private String signature;

    private Timestamp notificationDate;

    private String createdBy;

    private Timestamp createdDate;

    private Timestamp updatedDate;

    private String updatedBy;
}
