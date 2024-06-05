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
    private Integer pncPeriod;

    @Column(name = "pnc_date")
    private Timestamp pncDate;

    @Column(name = "ifa_tabs_given")
    private Integer ifaTabsGiven;

    @Column(name = "any_contraception_method")
    private Boolean anyContraceptionMethod;

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
    private Boolean motherDeath;

    @Column(name = "death_date")
    private Timestamp deathDate;

    @Column(name = "death_cause")
    private String causeOfDeath;

    @Column(name = "other_death_cause")
    private String otherDeathCause;

    @Column(name = "death_place")
    private String placeOfDeath;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
