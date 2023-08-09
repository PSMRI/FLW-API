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

    @Column(name = "severe_anemia")
    private String severeAnemia;

    @Column(name = "preg_induced_hypertension")
    private String pregInducedHypertension;

    @Column(name = "gest_diabetes_mellitus")
    private String gestDiabetesMellitus;

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

}
