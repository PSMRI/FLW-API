package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_stoptb_registration", schema = "db_iemr")
@Data
public class StopTBRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "benRegID")
    private Long benRegID;

    @Column(name = "providerServiceMapID")
    private Integer providerServiceMapID;

    @Column(name = "personFrom")
    private String personFrom;

    @Column(name = "caseFindingType")
    private String caseFindingType;

    @Column(name = "tuId")
    private Integer tuId;

    @Column(name = "tuName")
    private String tuName;

    @Column(name = "healthFacilityId")
    private Integer healthFacilityId;

    @Column(name = "healthFacilityName")
    private String healthFacilityName;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "temperatureValue")
    private Double temperatureValue;

    @Column(name = "isMobileAvailable")
    private Boolean isMobileAvailable;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "createdDate", insertable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "modifiedBy")
    private String modifiedBy;

    @Column(name = "lastModDate", insertable = false)
    private Timestamp lastModDate;

    @Column(name = "deleted")
    private Boolean deleted;
}
