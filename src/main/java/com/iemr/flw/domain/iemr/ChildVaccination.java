package com.iemr.flw.domain.iemr;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "t_childvaccinedetail1", schema = "db_iemr", catalog = "")
public class ChildVaccination {
    private long id;
    private Long beneficiaryRegId;
    private Long benVisitId;
    private String defaultReceivingAge;
    private Integer vaccineId;
    private String vaccineName;
    private Boolean status;
    private String actualReceivingAge;
    private Timestamp receivedDate;
    private String receivedFacilityName;
    private String sctcode;
    private String sctTerm;
    private String vaccinationreceivedat;
    private String vaccinatedBy;
    private Boolean deleted;
    private String processed;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp lastModDate;
    private Long vanSerialNo;
    private Integer vanId;
    private String vehicalNo;
    private Integer parkingPlaceId;
    private String syncedBy;
    private Timestamp syncedDate;
    private String reservedForChange;
    @Transient
    private Long beneficiaryId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "BeneficiaryRegID")
    public Long getBeneficiaryRegId() {
        return beneficiaryRegId;
    }

    public void setBeneficiaryRegId(Long beneficiaryRegId) {
        this.beneficiaryRegId = beneficiaryRegId;
    }

    @Basic
    @Column(name = "BenVisitID")
    public Long getBenVisitId() {
        return benVisitId;
    }

    public void setBenVisitId(Long benVisitId) {
        this.benVisitId = benVisitId;
    }

    @Basic
    @Column(name = "DefaultReceivingAge")
    public String getDefaultReceivingAge() {
        return defaultReceivingAge;
    }

    public void setDefaultReceivingAge(String defaultReceivingAge) {
        this.defaultReceivingAge = defaultReceivingAge;
    }

    @Basic
    @Column(name = "VaccineName")
    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    @Basic
    @Column(name = "vaccineId")
    public Integer getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Integer vaccineId) {
        this.vaccineId = vaccineId;
    }

    @Basic
    @Column(name = "Status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "ActualReceivingAge")
    public String getActualReceivingAge() {
        return actualReceivingAge;
    }

    public void setActualReceivingAge(String actualReceivingAge) {
        this.actualReceivingAge = actualReceivingAge;
    }

    @Basic
    @Column(name = "ReceivedDate")
    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Basic
    @Column(name = "ReceivedFacilityName")
    public String getReceivedFacilityName() {
        return receivedFacilityName;
    }

    public void setReceivedFacilityName(String receivedFacilityName) {
        this.receivedFacilityName = receivedFacilityName;
    }

    @Basic
    @Column(name = "Sctcode")
    public String getSctcode() {
        return sctcode;
    }

    public void setSctcode(String sctcode) {
        this.sctcode = sctcode;
    }

    @Basic
    @Column(name = "SctTerm")
    public String getSctTerm() {
        return sctTerm;
    }

    public void setSctTerm(String sctTerm) {
        this.sctTerm = sctTerm;
    }

    @Basic
    @Column(name = "Vaccinationreceivedat")
    public String getVaccinationreceivedat() {
        return vaccinationreceivedat;
    }

    public void setVaccinationreceivedat(String vaccinationreceivedat) {
        this.vaccinationreceivedat = vaccinationreceivedat;
    }

    @Basic
    @Column(name = "vaccinatedBy")
    public String getVaccinatedBy() {
        return vaccinatedBy;
    }

    public void setVaccinatedBy(String vaccinatedBy) {
        this.vaccinatedBy = vaccinatedBy;
    }

    @Basic
    @Column(name = "Deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Basic
    @Column(name = "Processed")
    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    @Basic
    @Column(name = "CreatedBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Basic
    @Column(name = "CreatedDate")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Basic
    @Column(name = "ModifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Basic
    @Column(name = "LastModDate")
    public Timestamp getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Timestamp lastModDate) {
        this.lastModDate = lastModDate;
    }

    @Basic
    @Column(name = "VanSerialNo")
    public Long getVanSerialNo() {
        return vanSerialNo;
    }

    public void setVanSerialNo(Long vanSerialNo) {
        this.vanSerialNo = vanSerialNo;
    }

    @Basic
    @Column(name = "VanID")
    public Integer getVanId() {
        return vanId;
    }

    public void setVanId(Integer vanId) {
        this.vanId = vanId;
    }

    @Basic
    @Column(name = "VehicalNo")
    public String getVehicalNo() {
        return vehicalNo;
    }

    public void setVehicalNo(String vehicalNo) {
        this.vehicalNo = vehicalNo;
    }

    @Basic
    @Column(name = "ParkingPlaceID")
    public Integer getParkingPlaceId() {
        return parkingPlaceId;
    }

    public void setParkingPlaceId(Integer parkingPlaceId) {
        this.parkingPlaceId = parkingPlaceId;
    }

    @Basic
    @Column(name = "SyncedBy")
    public String getSyncedBy() {
        return syncedBy;
    }

    public void setSyncedBy(String syncedBy) {
        this.syncedBy = syncedBy;
    }

    @Basic
    @Column(name = "SyncedDate")
    public Timestamp getSyncedDate() {
        return syncedDate;
    }

    public void setSyncedDate(Timestamp syncedDate) {
        this.syncedDate = syncedDate;
    }

    @Basic
    @Column(name = "ReservedForChange")
    public String getReservedForChange() {
        return reservedForChange;
    }

    public void setReservedForChange(String reservedForChange) {
        this.reservedForChange = reservedForChange;
    }
}
