package com.iemr.flw.domain.identity;

import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "m_beneficiaryregidmapping", schema = "db_identity")
@Data
public class RMNCHMBeneficiaryregidmapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long benRegId;

    @Column(nullable = false)
    private Long beneficiaryID;

    @Column(length = 30)
    private String createdBy;

    @Column(name = "CreatedDate", updatable = false)
    private Timestamp createdDate;

    private Boolean deleted = false;

    private Boolean provisioned;

    private Integer providerServiceMapID;

    private Boolean reserved;

    private Integer reservedById;

    @Column(length = 45)
    private String reservedFor;

    private Timestamp reservedOn;

    @Column(name = "VehicalNo")
    private String vehicalNo;

    private String syncedBy;

    private Timestamp syncedDate;

    private String reservedForChange;

    @Expose
    @Column(name = "vanID")
    private Integer vanID;

    @Expose
    @Column(name = "parkingPlaceID")
    private Integer parkingPlaceID;

    @Expose
    @Column(name = "VanSerialNo")
    private BigInteger vanSerialNo;
}
