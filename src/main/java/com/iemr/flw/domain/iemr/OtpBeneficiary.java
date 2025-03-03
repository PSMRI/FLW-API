package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "m_otp_beneficiary", schema = "db_iemr")
@Data
public class OtpBeneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "phoneNumber", nullable = false, length = 45)
    private String phoneNumber;

    @Column(name = "isOtpVerify")
    private Boolean isOtpVerify = false;

    @Column(name = "otp", nullable = false)
    private Integer otp;


    @Column(name = "createdAt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
}