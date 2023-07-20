package com.iemr.flw.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_screening", schema = "db_iemr")
@Data
public class TBScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long benId;

    private Timestamp visitDate;

    private Boolean coughMoreThan2Weeks;

    private Boolean bloodInSputum;

    private Boolean feverMoreThan2Weeks;

    private Boolean lossOfWeight;

    private Boolean nightSweats;

    private Boolean historyOfTb;

    private Boolean takingAntiTBDrugs;

    private Boolean familySufferingFromTB;
}
