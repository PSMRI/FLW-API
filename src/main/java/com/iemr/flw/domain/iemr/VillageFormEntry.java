package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "village_form_entry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageFormEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asha_id")
    private String ashaId;

    @Column(name = "form_type")
    private String formType;
    @Column(name = "VHND_date")
    private Date date;
    @Column(name = "place")
    private String place;

    @Column(name = "participant_count")
    private int participantCount;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "incentive_amount")
    private double incentiveAmount;

    @Column(name = "fmr_code")
    private String fmrCode;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}
