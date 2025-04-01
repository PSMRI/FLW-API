package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;



@Entity
@Data
@Table(name = "disease_leprosy",schema = "db_iemr")
public class LeprosyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_home_visit")
    private Date dateOfHomeVisit;

    @Column(name = "leprosy_status")
    private String leprosyStatus;

    @Column(name = "referred_to")
    private Integer referredTo;

    @Column(name = "other")
    private String other;

    @Column(name = "leprosy_status_date")
    private Date leprosyStatusDate;

    @Column(name = "type_of_leprosy")
    private String typeOfLeprosy;

    @Column(name = "follow_up_date")
    private String followUpDate;

    @Column(name = "status")
    private String status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeID;

    @Column(name = "ben_id")
    private BigInteger benId;
    @Column(name = "userID")
    Integer userID;
}

