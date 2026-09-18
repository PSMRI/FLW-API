package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "t_prescribeddrug", schema = "db_iemr")
@Data
public class PrescribedDrugDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PrescribedDrugID", insertable = false, updatable = false)
    private Long id;

    @Column(name = "BeneficiaryRegID")
    private Long beneficiaryRegID;

    @Column(name = "BenVisitID")
    private Long benVisitID;

    @Column(name = "ProviderServiceMapID")
    private Integer providerServiceMapID;

    @Column(name = "VisitCode")
    private Long visitCode;

    @Column(name = "PrescriptionID")
    private Long prescriptionID;

    @Column(name = "GenericDrugName")
    private String drugName;

    @Column(name = "Dose")
    private String dose;

    @Column(name = "Frequency")
    private String frequency;

    @Column(name = "Duration")
    private String duration;

    @Column(name = "DuartionUnit")
    private String durationUnit;

    @Column(name = "SpecialInstruction")
    private String specialInstruction;

    @Column(name = "Deleted", insertable = false, updatable = true)
    private Boolean deleted;

    @Column(name = "Processed", insertable = false, updatable = true)
    private String processed;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate", insertable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "ModifiedBy")
    private String modifiedBy;

    @Column(name = "LastModDate", insertable = false, updatable = false)
    private Timestamp lastModDate;

    @Column(name = "VanSerialNo")
    private Long vanSerialNo;

    @Column(name = "VanID")
    private Integer vanID;

    @Column(name = "ParkingPlaceID")
    private Integer parkingPlaceID;

    @Column(name = "SyncedBy")
    private String syncedBy;

    @Column(name = "SyncedDate")
    private Timestamp syncedDate;
}
