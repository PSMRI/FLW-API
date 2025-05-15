package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

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
    private Integer userId;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "health_status",length = 50)
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

    @Column(name = "place",length = 100)
    private String place;

    @Column(name = "referred_to_health_facility",length = 100)
    private String referredToHealthFacility;

    @Column(name = "counseling_provided")
    private Boolean counselingProvided;

    @Column(name = "counseling_type",length = 50)
    private String counselingType;

    @Column(name = "follow_up_date")
    private Date followUpDate;

    @Column(name = "referral_status",length = 50)
    private String referralStatus;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "created_by")
    private String createdBy;


}
