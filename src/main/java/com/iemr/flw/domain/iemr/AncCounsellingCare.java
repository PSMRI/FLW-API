package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "anc_counselling_care", schema = "db_iemr")
@Data
public class AncCounsellingCare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_id", nullable = false)
    private Long beneficiaryId;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "home_visit_date", nullable = false)
    private LocalDate homeVisitDate;

    @Column(name = "anc_visit_id", nullable = false)
    private Long ancVisitId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ---------- BOOLEAN FLAGS ---------- */

    @Column(name = "select_all")
    private Boolean selectAll = false;

    @Column(name = "swelling")
    private Boolean swelling = false;

    @Column(name = "high_bp")
    private Boolean highBp = false;

    @Column(name = "convulsions")
    private Boolean convulsions = false;

    @Column(name = "anemia")
    private Boolean anemia = false;

    @Column(name = "reduced_fetal_movement")
    private Boolean reducedFetalMovement = false;

    @Column(name = "age_risk")
    private Boolean ageRisk = false;

    @Column(name = "child_gap")
    private Boolean childGap = false;

    @Column(name = "short_height")
    private Boolean shortHeight = false;

    @Column(name = "pre_preg_weight")
    private Boolean prePregWeight = false;

    @Column(name = "bleeding")
    private Boolean bleeding = false;

    @Column(name = "miscarriage_history")
    private Boolean miscarriageHistory = false;

    @Column(name = "four_plus_delivery")
    private Boolean fourPlusDelivery = false;

    @Column(name = "first_delivery")
    private Boolean firstDelivery = false;

    @Column(name = "twin_pregnancy")
    private Boolean twinPregnancy = false;

    @Column(name = "c_section_history")
    private Boolean cSectionHistory = false;

    @Column(name = "pre_existing_disease")
    private Boolean preExistingDisease = false;

    @Column(name = "fever_malaria")
    private Boolean feverMalaria = false;

    @Column(name = "jaundice")
    private Boolean jaundice = false;

    @Column(name = "sickle_cell")
    private Boolean sickleCell = false;

    @Column(name = "prolonged_labor")
    private Boolean prolongedLabor = false;

    @Column(name = "malpresentation")
    private Boolean malpresentation = false;

    /* ---------- Lifecycle Hooks ---------- */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ---------- Getters & Setters ---------- */
    // Lombok use kar raha ho toh @Getter @Setter laga sakta hai
}
