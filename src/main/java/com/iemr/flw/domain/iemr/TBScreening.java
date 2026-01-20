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
}
