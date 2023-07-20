package com.iemr.flw.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_screening", schema = "db_iemr")
@Data
public class TBSuspected {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long benId;

    private Timestamp visitDate;

    private Boolean isSputumCollected;

    private String sputumSubmittedAt;

    private String nikshayId;

    private String sputumTestResult;

    private Boolean referred;

    private String followUps;
}
