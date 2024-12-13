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
}
