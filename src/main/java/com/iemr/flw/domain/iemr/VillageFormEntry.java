package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "village_form_entry",schema = "db_iemr")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageFormEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;
    @Column(name = "userId")
    private Integer userId;

    @Column(name = "form_type")
    private String formType;
    @Column(name = "form_date")
    private Date date;
    @Column(name = "place")
    private String place;

    @Column(name = "participant_count")
    private int participantCount;

    @Column(name = "image_urls")
    private String imageUrls;


    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "age_group")
    private Integer age;

    @Column(name = "location")
    private String location;

    @Column(name = "dewormingRound")
    private Boolean dewormingRound;
}
