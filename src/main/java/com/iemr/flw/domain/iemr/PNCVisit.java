package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_pnc_visit")
@Data
public class PNCVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

//    @Column(name = "pnc_visit")
//    private Integer pncVisit;

    @Column(name = "pnc_period")
    private String pncPeriod;

    @Column(name = "pnc_date")
    private String pncDate;

    @Column(name = "ifa_tabs_given")
    private String ifaTabsGiven;

    @Column(name = "any_contraception_method")
    private String anyContraceptionMethod;

    @Column(name = "contraception_method")
    private String contraceptionMethod;

    @Column(name = "other_ppc_method")
    private String otherPpcMethod;

    @Column(name = "mother_danger_sign")
    private String motherDangerSign;

    @Column(name = "other_danger_sign")
    private String otherDangerSign;

    @Column(name = "referral_facility")
    private String referralFacility;

    @Column(name = "mother_death")
    private String motherDeath;

    @Column(name = "death_date")
    private String deathDate;

    @Column(name = "death_cause")
    private String causeOfDeath;

    @Column(name = "other_death_cause")
    private String otherDeathCause;

    @Column(name = "death_place")
    private String placeOfDeath;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
