package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "m_immunizationservicevaccination", schema = "db_iemr")
@Data
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VaccinationID")
    private short vaccineId;

    @Column(name = "Currentimmunizationservice")
    private String immunizationService;

    @Column(name = "Currentimmunizationserviceid")
    private Integer immunizationServiceId;

    @Column(name = "VisitCategoryID")
    private Integer visitCategoryId;

    @Column(name = "VaccineName")
    private String vaccineName;

    @Column(name = "minAllowedAgeInMillis")
    private Long minAllowedAgeInMillis;

    @Column(name = "maxAllowedAgeInMillis")
    private Long maxAllowedAgeInMillis;

    @Column(name = "category")
    private String category;

    @Column(name = "overdueDurationSinceMinInMillis")
    private Long overdueDurationSinceMinInMillis;

    @Column(name = "dependantVaccineId")
    private Integer dependantVaccineId;

    @Column(name = "dependantCoolDuration")
    private Long dependantCoolDuration;

    @Column(name = "Sctcode")
    private String sctcode;

    @Column(name = "SctTerm")
    private String sctTerm;

    @Column(name = "Deleted")
    private Boolean deleted;

    @Column(name = "Processed")
    private String processed;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "ModifiedBy")
    private String modifiedBy;

    @Column(name = "LastModDate")
    private Timestamp lastModDate;

}
