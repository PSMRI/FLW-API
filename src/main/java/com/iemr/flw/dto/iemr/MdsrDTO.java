package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MdsrDTO {
    private Long id;

    private Long benId;

    private Timestamp dateOfDeath;

    private String address;

    private String husbandName;

    private String causeOfDeath;

    private String reasonDeath;

    private Timestamp investigationDate;

    private Boolean actionTaken;

    private String signature;

    private Timestamp dateIc;

    private String createdBy;

    private Timestamp createdDate;

    private Timestamp updatedDate;

    private String updatedBy;
}
