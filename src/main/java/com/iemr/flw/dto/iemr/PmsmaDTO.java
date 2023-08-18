package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PmsmaDTO {
    private Long id;
    private Long benId;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;
}
