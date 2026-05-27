package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
@Data
public class SammelanRequestDTO {

    private Integer ashaId;                    // ASHA worker ID
    private Long date;                 // Meeting date
    private String place;                   // Dropdown: HWC / Anganwadi Centre / Community Center
    private Integer participants;

    @JsonIgnore
    private MultipartFile[] sammelanImages; // up to 5 images
// Number of participants attended

}
