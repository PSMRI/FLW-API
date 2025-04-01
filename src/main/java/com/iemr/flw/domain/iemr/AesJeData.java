package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "disease_aesje",schema = "db_iemr")
public class AesJeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "case_date")
    private Date caseDate;

    @Column(name = "aesje_case_status")
    private Boolean aesjeCaseStatus;

    @Column(name = "referred_to")
    private Integer referredTo;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeID;

    @Column(name = "ben_id")
    private BigInteger benId;

    @Column(name = "userID")
    Integer userID;
}
