package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class UwinSessionRequestDTO {
    private Integer ashaId;
    private Timestamp date;
    private String place;
    private Integer participants;
    private MultipartFile[] attachments;
    private String createdBy;
}
