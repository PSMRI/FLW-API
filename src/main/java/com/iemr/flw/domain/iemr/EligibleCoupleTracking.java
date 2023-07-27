package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ELIGIBLE_COUPLE_TRACKING", schema = "db_iemr")
@Data
public class EligibleCoupleTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ecr_id")
    private Long ecrId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "is_pregnancy_test_done")
    private String isPregnancyTestDone;

    @Column(name = "pregnancy_test_result")
    private String pregnancyTestResult;

    @Column(name = "is_pregnant")
    private String isPregnant;

    @Column(name = "using_family_planning")
    private Boolean usingFamilyPlanning;

    @Column(name = "method_of_contraception")
    private String methodOfContraception;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;
}
