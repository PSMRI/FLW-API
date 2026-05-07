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

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    // Vitals
    @Column(name = "pulse")
    private Integer pulse;

    @Column(name = "systolic_bp")
    private Integer systolicBP;

    @Column(name = "diastolic_bp")
    private Integer diastolicBP;

    @Column(name = "rbs_value")
    private Double rbsValue;

    // Clinical signs
    @Column(name = "pallor")
    private Boolean pallor;

    @Column(name = "icterus")
    private Boolean icterus;

    @Column(name = "lymphadenopathy")
    private Boolean lymphadenopathy;

    @Column(name = "edema")
    private Boolean edema;

    @Column(name = "cyanosis")
    private Boolean cyanosis;

    @Column(name = "clubbing")
    private Boolean clubbing;

    // JSON array of selected key population risk factors
    @Column(name = "key_population_risk_factors", columnDefinition = "TEXT")
    private String keyPopulationRiskFactors;

    // Positive | Reactive | Negative | Unknown
    @Column(name = "hiv_status")
    private String hivStatus;

    // Server-calculated from vitals thresholds
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
