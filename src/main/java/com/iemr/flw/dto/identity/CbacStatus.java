package com.iemr.flw.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CbacStatus {
    private Long benId;
    private Timestamp createdDate;
    private String status;
}
