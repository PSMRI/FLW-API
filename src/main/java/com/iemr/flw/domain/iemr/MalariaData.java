package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "disease_malaria",schema = "db_iemr")
public class MalariaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "ben_id")
    BigInteger benId;

    @Column(name = "case_date")
    Date caseDate;

    @Column(name = "case_status")
    String caseStatus;

    @Column(name = "symptoms")
    String symptoms;

    @Column(name = "malaria_case_count")
    String malariaCaseCount;

    @Column(name = "referred_to")
    Integer ReferredTo;

    @Column(name = "malaria_case_status_date")
    Date malariaCaseStatusDate;

    @Column(name = "remarks")
    String  Remarks;

    @Column(name = "follow_up")
    Integer followUp;

    @Column(name = "irs_date")
    Date irsDate;

    @Column(name = "rounds")
    Integer rounds;

    @Column(name = "DiseaseTypeID")
    Integer diseaseTypeID;

    @Column(name = "userID")
    Integer userID;
}



