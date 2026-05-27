package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.SammelanAttachment;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class SammelanResponseDTO {

    private Long id;
    private Integer ashaId;
    private Long date;
    private String place;
    private Integer participants;
    private List<String> imagePaths;



}
