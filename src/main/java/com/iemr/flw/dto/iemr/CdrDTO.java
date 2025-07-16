package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Data
public class CdrDTO {
    private Long id;

    private Long benId;

    private String motherName;

    private String fatherName;

    private Timestamp visitDate;

    private String address;

    private String houseNo;

    private String colony;

    private Integer pincode;

    private String landmarks;

    private Long landline;

    private Long mobile;

    private Timestamp deathDate;

    private String placeOfDeath;

    private String nameOfInformant;

    private String deathTime;

    private String signature;

    private Timestamp notificationDate;

    private String createdBy;

    private Timestamp createdDate;

    private String cdrImage2;

    private String deathCertImage1;

    private String deathCertImage2;

    private Timestamp updatedDate;

    private String updatedBy;
}
