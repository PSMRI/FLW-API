package com.iemr.flw.domain.iemr;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_stoptb_diagnostics", schema = "db_iemr")
@Data
public class StopTBDiagnostics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    // PRD: user-provided date, not editable once submitted
    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "nikshay_id", length = 50)
    private String nikshayId;

    // Digital Chest X-ray
    @Column(name = "is_referred_for_digital_chest_xray")
    private Boolean isReferredForDigitalChestXray;

    @Column(name = "reason_for_denial_chest_xray", length = 255)
    private String reasonForDenialChestXray;

    @Column(name = "reason_for_denial_chest_xray_other", length = 255)
    private String reasonForDenialChestXrayOther;

    @Column(name = "is_digital_chest_xray_conducted_id")
    private Integer isDigitalChestXrayConductedId;

    @Column(name = "is_digital_chest_xray_conducted")
    private Boolean isDigitalChestXrayConducted;

    @Column(name = "reason_not_conducted_chest_xray", length = 255)
    private String reasonNotConductedChestXray;

    @Column(name = "reason_not_conducted_chest_xray_other", length = 255)
    private String reasonNotConductedChestXrayOther;

    // Positive | Negative
    @Column(name = "digital_chest_xray_result_id")
    private Integer digitalChestXrayResultId;

    @Column(name = "digital_chest_xray_result", length = 20)
    private String digitalChestXrayResult;

    // Sputum Collection
    @Column(name = "is_referred_for_sputum_collection")
    private Boolean isReferredForSputumCollection;

    @Column(name = "reason_for_denial_sputum", length = 500)
    private String reasonForDenialSputum;

    @Column(name = "reason_for_denial_sputum_other", length = 255)
    private String reasonForDenialSputumOther;

    @Column(name = "sputum_submitted_at", length = 255)
    private String sputumSubmittedAt;

    // Truenat / NAAT
    @Column(name = "is_truenat_conducted_id")
    private Integer isTruenatConductedId;

    @Column(name = "is_truenat_conducted")
    private Boolean isTruenatConducted;

    @Column(name = "reason_not_conducted_naat", length = 255)
    private String reasonNotConductedNaat;

    @Column(name = "reason_not_conducted_naat_other", length = 255)
    private String reasonNotConductedNaatOther;

    // Positive | Negative
    @Column(name = "truenat_result_id")
    private Integer truenatResultId;

    @Column(name = "truenat_result", length = 20)
    private String truenatResult;

    // Liquid Culture — enabled if History of TB = Yes AND Anti-TB drugs = Yes
    @Column(name = "recommended_for_liquid_culture_id")
    private Integer recommendedForLiquidCultureId;

    @Column(name = "recommended_for_liquid_culture")
    private Boolean recommendedForLiquidCulture;

    // Positive | Negative — editable after submission (results come after 40-45 days)
    @Column(name = "liquid_culture_result_id")
    private Integer liquidCultureResultId;

    @Column(name = "liquid_culture_result", length = 20)
    private String liquidCultureResult;

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
