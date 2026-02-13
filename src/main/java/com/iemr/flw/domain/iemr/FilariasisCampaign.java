package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_filariasis_mda",schema = "db_iemr")
@Data
public class FilariasisCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "no_of_families", nullable = false)
    private Integer numberOfFamilies = 0;

    @Column(name = "no_of_individuals", nullable = false)
    private Integer numberOfIndividuals = 0;

    /**
     * Store JSON array like ["img1.jpg","img2.jpg"]
     * MySQL JSON column
     */
    @Column(name = "mda_photos", columnDefinition = "json")
    private String campaignPhotos;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    @Column(name = "updated_by", length = 200)
    private String updatedBy;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    /* ---------- Auto timestamps ---------- */

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }


}
