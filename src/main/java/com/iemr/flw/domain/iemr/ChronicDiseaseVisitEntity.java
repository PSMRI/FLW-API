package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cdtf_visit_details")
@Data
public class ChronicDiseaseVisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_id")
    private Long benId;

    @Column(name = "household_id")
    private Long hhId;

    @Column(name = "form_id")
    private String formId;

    @Column(name =   "version")
    private Integer version;

    @Column(name = "visit_no")
    private Integer visitNo;

    @Column(name = "follow_up_no")
    private Integer followUpNo;

    @Column(name = "diagnosis_codes", columnDefinition = "TEXT")
    private String diagnosisCodes;

    @Column(name = "treatment_start_date")
    private LocalDate treatmentStartDate;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "form_data_json", columnDefinition = "JSON")
    private String formDataJson;

    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // 🔹 Auto timestamps
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
