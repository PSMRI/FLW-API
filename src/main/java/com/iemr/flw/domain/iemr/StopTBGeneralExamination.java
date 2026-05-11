package com.iemr.flw.domain.iemr;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_stoptb_general_examination", schema = "db_iemr")
@Data
public class StopTBGeneralExamination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_reg_id")
    private Long beneficiaryRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    // Vitals
    @Column(name = "pulse_rate")
    private Integer pulseRate;

    @Column(name = "systolic_bp")
    private Integer systolicBP;

    @Column(name = "diastolic_bp")
    private Integer diastolicBP;

    @Column(name = "random_blood_sugar")
    private Double randomBloodSugar;

    // Clinical signs — ID + label ("PRESENT" / "ABSENT")
    @Column(name = "pallor_id")
    private Integer pallorId;

    @Column(name = "pallor")
    private String pallor;

    @Column(name = "icterus_id")
    private Integer icterusId;

    @Column(name = "icterus")
    private String icterus;

    @Column(name = "lymphadenopathy_id")
    private Integer lymphadenopathyId;

    @Column(name = "lymphadenopathy")
    private String lymphadenopathy;

    @Column(name = "oedema_id")
    private Integer oedemaId;

    @Column(name = "oedema")
    private String oedema;

    @Column(name = "cyanosis_id")
    private Integer cyanosisId;

    @Column(name = "cyanosis")
    private String cyanosis;

    @Column(name = "clubbing_id")
    private Integer clubbingId;

    @Column(name = "clubbing")
    private String clubbing;

    // JSON arrays serialised from mobile
    @Column(name = "key_population_risk_factor_ids", columnDefinition = "TEXT")
    private String keyPopulationRiskFactorIds;

    @Column(name = "key_population_risk_factors", columnDefinition = "TEXT")
    private String keyPopulationRiskFactors;

    @Column(name = "hiv_status_id")
    private Integer hivStatusId;

    // "Positive" | "Reactive" | "Negative" | "Unknown"
    @Column(name = "hiv_status")
    private String hivStatus;

    @Column(name = "referral_to_hwc_needed_id")
    private Integer referralToHWCNeededId;

    @Column(name = "referral_to_hwc_needed")
    private Boolean referralToHWCNeeded;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "last_mod_date")
    private Timestamp lastModDate;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
