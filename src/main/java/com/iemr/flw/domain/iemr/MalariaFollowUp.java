package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "malaria_follow_up", schema = "db_iemr")
@Data
public class MalariaFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id", nullable = false)
    private Long benId;

    @Column(name = "house_hold_details_id", nullable = false)
    private Long houseHoldDetailsId;

    @Column(name = "user_Id")
    private Integer userId;

    @Column(name = "disease_Id", nullable = false)
    private Long diseaseId;

    @Column(name = "date_of_diagnosis", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfDiagnosis;

    @Column(name = "treatment_start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date treatmentStartDate;

    @Column(name = "treatment_given", nullable = false)
    private String treatmentGiven;

    @Column(name = "days", nullable = false)
    private String day;

    @Column(name = "treatment_completion_date")
    @Temporal(TemporalType.DATE)
    private Date treatmentCompletionDate;

    @Column(name = "referral_date")
    @Temporal(TemporalType.DATE)
    private Date referralDate;
}
