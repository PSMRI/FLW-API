package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "disease_kalaazar",schema = "db_iemr")
public class KalaAzarData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ben_id")
    BigInteger benId;

    @Column(name = "case_date")
    private Date caseDate;

    @Column(name = "case_status", length = 225)
    private String caseStatus;

    @Column(name = "symptoms", length = 225)
    private String symptoms;

    @Column(name = "malaria_case_count", length = 225)
    private String malariaCaseCount;

    @Column(name = "referred_to")
    private Long referredTo;

    @Column(name = "kalaAzar_case_status_date")
    private Date kalaAzarCaseStatusDate;

    @Column(name = "remarks", length = 225)
    private String remarks;

    @Column(name = "follow_up_point", length = 225)
    private String followUpPoint;

    @Column(name = "follow_up_date")
    private Date followUpDate;

    @Column(name = "disease_status", length = 22)
    private String status;

    @Column(name = "DiseaseTypeID")
    Integer diseaseTypeID;

    @Column(name = "userID")
    Integer userID;

}
