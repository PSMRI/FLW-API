package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "maa_meeting", schema = "db_iemr")
public class MaaMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meeting_date")
    private LocalDate meetingDate;

    @Column(name = "place")
    private String place;

    @Column(name = "participants")
    private Integer participants;

    @Column(name = "quarter")
    private Integer quarter;

    @Column(name = "year")
    private Integer year;

    @Column(name = "asha_id")
    private Integer ashaId;

    // Store multiple images as JSON of base64 strings
    @Lob
    @Column(name = "meeting_images", columnDefinition = "LONGTEXT")
    private String meetingImagesJson;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "no_of_pragnent_women")
    private Integer noOfPragnentWomen;

    @Column(name = "no_of_lacting_mother")
    private Integer noOfLactingMother;

    @Column(name = "mitanin_activity_checkList")
    private String mitaninActivityCheckList;

    @Column(name = "created_by")
    private String createdBy;
}
