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
}
