package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
@Data
@Entity
@Table(name = "disease")
public class DiseaseControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "case_date", nullable = false)
    private Timestamp caseDate ; // Auto-populated as today's date, non-editable

    @Enumerated(EnumType.STRING)
    @Column(name = "case_status", nullable = false)
    private Integer caseStatus; // Dropdown for case status

    @ElementCollection
    @CollectionTable(name = "malaria_symptoms", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "symptom")
    private List<String> symptoms; // List of symptoms

    @Column(name = "malaria_case_count", nullable = false)
    private int malariaCaseCount; // Auto-updated based on confirmed/treatment cases

    @Enumerated(EnumType.STRING)
    @Column(name = "referred_to", nullable = false)
    private Integer referredTo; // Dropdown for referral options

    @Column(name = "other_referred_to")
    private String otherReferredTo; // Required if "Referred To" = "Other"

    @Column(name = "malaria_case_status_date")
    private Timestamp malariaCaseStatusDate; // Auto-populated when status is updated

    @Column(name = "remarks")
    private String remarks; // Optional field for additional notes

    @Column(name = "follow_up_point")
    private Integer followUpPoint; // Single-select radio button (1-6)

    @Column(name = "follow_up_date")
    private Timestamp followUpDate; // Follow-up date

    // Enums for case status and referral options
    public enum CaseStatus {
        SUSPECTED,
        CONFIRMED,
        NOT_CONFIRMED,
        TREATMENT_GIVEN
    }

    public enum ReferredTo {
        PRIMARY_HEALTH_CENTRE,
        COMMUNITY_HEALTH_CENTRE,
        DISTRICT_HOSPITAL,
        MEDICAL_COLLEGE_AND_HOSPITAL,
        REFERRAL_HOSPITAL,
        OTHER_PRIVATE_HOSPITAL,
        OTHER,
        NONE
    }
}
