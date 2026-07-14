package com.iemr.flw.dto.iemr;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class MaaMeetingResponseDTO {
    @Schema(description = "Unique ID of the meeting", example = "1")
    private Long id;

    @Schema(description = "Date of the meeting", example = "2025-09-06")
    private LocalDate meetingDate;

    @Schema(description = "Place of the meeting", example = "HWC")
    private String place;

    @Schema(description = "Number of participants attended", example = "25")
    private Integer participants;

    @Schema(description = "Quarter of the year", example = "3")
    private Integer quarter;

    @Schema(description = "Year of the meeting", example = "2025")
    private Integer year;

    @Schema(description = "ID of ASHA", example = "963")
    private Integer ashaId;

    @Schema(description = "Meeting images in base64 array",
            example = "[\"iVBORw0KGgoAAAANSUhEUgAA...\", \"iVBORw0KGgoAAAANSUhEUgBB...\"]")
    private List<String > meetingImages;

    private String villageName;
    private String noOfPragnentWomen;
    private String noOfLactingMother;
    private String mitaninActivityCheckList;

    @Column(name = "created_by")
    private String createdBy;


}
