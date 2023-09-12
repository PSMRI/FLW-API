package com.iemr.flw.domain.iemr;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_pregnant_woman_register", schema = "db_iemr")
@Data
public class PregnantWomanRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "registration_date")
    private Timestamp registrationDate;

    @Column(name = "rch_id")
    private Long rchId;

    @Column(name = "mcp_card_id")
    private Long mcpCardId;

    @Column(name = "lmp_date")
    private Timestamp lmpDate;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "height")
    private Integer height;

    @Column(name = "rpr_test_result")
    private String rprTestResult;

    @Column(name = "date_of_rpr_test")
    private Timestamp dateOfRprTest;

    @Column(name = "hiv_test_result")
    private String hivTestResult;

    @Column(name = "hbs_ag_test_result")
    private String hbsAgTestResult;

    @Column(name = "date_of_hiv_test")
    private Timestamp dateOfHivTest;

    @Column(name = "date_of_hbs_ag_test")
    private Timestamp dateOfHbsAgTest;

    @Column(name = "past_illness")
    private String pastIllness;

    @Column(name = "other_past_illness")
    private String otherPastIllness;

    @Column(name = "is_first_pregnancy_test")
    private Boolean isFirstPregnancyTest;

    @Column(name = "num_prev_pregnancy")
    private Integer numPrevPregnancy;

    @Column(name = "preg_complication")
    private String pregComplication;

    @Column(name = "other_complication")
    private String otherComplication;

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

