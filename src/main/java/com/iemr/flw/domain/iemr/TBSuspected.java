package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_suspected", schema = "db_iemr")
@Data
public class TBSuspected {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "is_sputum_collected")
    private Boolean isSputumCollected;

    @Column(name = "sputum_submitted")
    private String sputumSubmittedAt;

    @Column(name = "nikshay_id")
    private String nikshayId;

    @Column(name = "sputum_test_result")
    private String sputumTestResult;

    @Column(name = "is_referred")
    private Boolean referred;

    @Column(name = "followups")
    private String followUps;

    // Visit Information
    @Column(name = "visit_label")
    private String visitLabel;

    @Column(name = "type_of_tb_case")
    private String typeOfTBCase;

    @Column(name = "reason_for_suspicion", length = 500)
    private String reasonForSuspicion;


    // Chest X-Ray
    @Column(name = "is_chest_xray_done")
    private Boolean isChestXRayDone;

    @Column(name = "chest_xray_result", length = 100)
    private String chestXRayResult;

    // Referral & Confirmation
    @Column(name = "referral_facility", length = 200)
    private String referralFacility;

    @Column(name = "is_tb_confirmed")
    private Boolean isTBConfirmed;

    @Column(name = "is_drtb_confirmed")
    private Boolean isDRTBConfirmed;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapId;

    @Column(name = "is_confirmed")
     private Boolean isConfirmed;


}
