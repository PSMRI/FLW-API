package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "adolescent_health")
public class AdolescentHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private BigInteger benId;

    @Column(name = "userID")
    private Long userID;

    @Column(name = "visit_date")
    private Date visitDate;

    @Column(name = "health_status")
    private String healthStatus;

    @Column(name = "ifa_tablet_distribution")
    private Boolean ifaTabletDistribution;

    @Column(name = "quantity_of_ifa_tablets")
    private Integer quantityOfIfaTablets;

    @Column(name = "menstrual_hygiene_awareness_given")
    private Boolean menstrualHygieneAwarenessGiven;

    @Column(name = "sanitary_napkin_distributed")
    private Boolean sanitaryNapkinDistributed;

    @Column(name = "no_of_packets_distributed")
    private Integer noOfPacketsDistributed;

    @Column(name = "place")
    private String place;

    @Column(name = "referred_to_health_facility")
    private String referredToHealthFacility;

    @Column(name = "counseling_provided")
    private Boolean counselingProvided;

    @Column(name = "counseling_type")
    private String counselingType;

    @Column(name = "follow_up_date")
    private Date followUpDate;

    @Column(name = "referral_status")
    private String referralStatus;

    @Column(name = "userID")
    private Integer userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;



}
