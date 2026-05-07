package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_screening", schema = "db_iemr")
@Data
public class TBScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "cough_check")
    private Boolean coughMoreThan2Weeks;

    @Column(name = "blood_check")
    private Boolean bloodInSputum;

    @Column(name = "fever_check")
    private Boolean feverMoreThan2Weeks;

    @Column(name = "weight_check")
    private Boolean lossOfWeight;

    @Column(name = "sweats_check")
    private Boolean nightSweats;

    @Column(name = "history_check")
    private Boolean historyOfTb;

    @Column(name = "drugs_check")
    private Boolean takingAntiTBDrugs;

    @Column(name = "family_check")
    private Boolean familySufferingFromTB;

    @Column(name = "rise_of_fever")
    private Boolean riseOfFever;

    @Column(name = "loss_of_appetite")
    private Boolean lossOfAppetite;

    @Column(name = "age")
    private Boolean age;

    @Column(name = "diabetic")
    private Boolean diabetic;

    @Column(name = "tobacco_user")
    private Boolean tobaccoUser;

    @Column(name = "bmi")
    private Boolean bmi;

    @Column(name = "contact_with_tb_patient")
    private Boolean contactWithTBPatient;

    @Column(name = "history_of_tb_in_last_five_yrs")
    private Boolean historyOfTBInLastFiveYrs;

   @Column(name = "sympotomatic")
    private  String sympotomatic;

   @Column(name = "asymptomatic")
    private String asymptomatic;

    @Column(name = "recommandate_test")
    private String recommandateTest;

    // Added for Stop TB nurse flow — nullable, not used by ASHA flow
    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "deleted", insertable = false)
    private Boolean deleted;

    // Diagnostics / referrals — Stop TB nurse flow only
    @Column(name = "referred_for_digital_chest_xray")
    private Boolean referredForDigitalChestXray;

    @Column(name = "referred_for_sputum_collection")
    private Boolean referredForSputumCollection;

    @Column(name = "sputum_sample_submitted_at", length = 50)
    private String sputumSampleSubmittedAt;

    @Column(name = "recommended_for_truenat")
    private Boolean recommendedForTruenat;

    @Column(name = "recommended_for_liquid_culture")
    private Boolean recommendedForLiquidCulture;

    // JSON array of denial reasons when patient refuses a test
    @Column(name = "test_denial_reasons", columnDefinition = "TEXT")
    private String testDenialReasons;
}
