package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.apache.poi.hssf.record.TabIdRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class UwinSessionResponseDTO {
    private Long id;
    private Integer ashaId;
    private Timestamp date;
    private String place;
    private Integer participants;
    private List<String> attachments;
}
