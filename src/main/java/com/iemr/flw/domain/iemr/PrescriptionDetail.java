package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "t_prescription", schema = "db_iemr")
@Data
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PrescriptionID", insertable = false, updatable = false)
    private Long prescriptionID;

    @Column(name = "BeneficiaryRegID")
    private Long beneficiaryRegID;

    @Column(name = "BenVisitID")
    private Long benVisitID;

    @Column(name = "ProviderServiceMapID")
    private Integer providerServiceMapID;

    @Column(name = "VisitCode")
    private Long visitCode;

    @Column(name = "DiagnosisProvided")
    private String diagnosisProvided;

    @Column(name = "Instruction")
    private String instruction;

    @Column(name = "Remarks")
    private String remarks;

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
