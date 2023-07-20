package com.iemr.flw.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_infant_register")
@Data
public class InfantRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "baby_name")
    private String babyName;

    @Column(name = "infant_term")
    private String infantTerm;

    @Column(name = "corticosteroid_given")
    private String corticosteroidGiven;

    @Column(name = "gender")
    private String gender;

    @Column(name = "cried_at_birth")
    private Boolean babyCriedAtBirth;

    @Column(name = "resuscitation")
    private Boolean resuscitation;

    @Column(name = "referred")
    private String referred;

    @Column(name = "had_birth_defect")
    private String hadBirthDefect;

    @Column(name = "birth_defect")
    private String birthDefect;

    @Column(name = "other_defect")
    private String otherDefect;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "breast_feeding_started")
    private Boolean breastFeedingStarted;

    @Column(name = "opv0_dose")
    private Timestamp opv0Dose;

    @Column(name = "bcg_dose")
    private Timestamp bcgDose;

    @Column(name = "hepB_dose")
    private Timestamp hepBDose;

    @Column(name = "vitk_dose")
    private Timestamp vitkDose;
}
