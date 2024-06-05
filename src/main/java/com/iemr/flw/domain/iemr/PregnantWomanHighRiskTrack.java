package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "pregnant_high_risk_track", schema = "db_iemr")
@Data
public class PregnantWomanHighRiskTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "rd_pmsa")
    private String rdPmsa;

    @Column(name = "rd_dengue")
    private String rdDengue;

    @Column(name = "rd_filaria")
    private String rdFilaria;

    @Column(name = "severe_anemia")
    private String severeAnemia;

    @Column(name = "hemoglobin_test")
    private String hemoglobinTest;

    @Column(name = "ifa_given")
    private String ifaGiven;

    @Column(name = "ifa_quantity")
    private Integer ifaQuantity;

    @Column(name = "preg_induced_hypertension")
    private String pregInducedHypertension;

    @Column(name = "systolic")
    private Integer systolic;

    @Column(name = "diastolic")
    private Integer diastolic;

    @Column(name = "gest_diabetes_mellitus")
    private String gestDiabetesMellitus;

    @Column(name = "blood_glucose_test")
    private String bloodGlucoseTest;

    @Column(name = "fbg")
    private Integer fbg;

    @Column(name = "rbg")
    private Integer rbg;

    @Column(name = "ppbg")
    private Integer ppbg;

    @Column(name = "fasting_ogtt")
    private Integer fastingOgtt;

    @Column(name = "after2hrs_ogtt")
    private Integer after2hrsOgtt;

    @Column(name = "hypothyrodism")
    private String hypothyrodism;

    @Column(name = "polyhydromnios")
    private String polyhydromnios;

    @Column(name = "oligohydromnios")
    private String oligohydromnios;

    @Column(name = "antepartum_hem")
    private String antepartumHem;

    @Column(name = "mal_presentation")
    private String malPresentation;

    @Column(name = "hivsyph")
    private String hivsyph;

    @Column(name = "visit")
    private String visit;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
