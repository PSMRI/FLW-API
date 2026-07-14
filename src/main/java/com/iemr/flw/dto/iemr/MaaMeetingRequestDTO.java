package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class MaaMeetingRequestDTO {
    private Long id;
    private LocalDate meetingDate;
    private String place;
    private Integer participants;
    private MultipartFile[] meetingImages; // up to 5 images
    private Integer ashaId;
    private String villageName;
    private Integer noOfPragnentWomen;
    private Integer noOfLactingMother;
    private String  mitaninActivityCheckList;
    private String createdBY;
}
