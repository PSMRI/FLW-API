package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "t_benvisitdetail", schema = "db_iemr")
public class BenVisitDetail {
    private long benVisitId;
    private Long beneficiaryRegId;
    private Long visitCode;
    private Timestamp visitDateTime;
    private Short visitNo;
    private String visitReason;
    private String visitCategory;
    private String subVisitCategory;
    private String pregnancyStatus;
    private String rchid;
    private String healthFacilityType;
    private String healthFacilityLocation;
    private String reportFilePath;
    private String healthId;
    private String healthIdNumber;
    private Timestamp carecontextLinkDate;
    private String screeningtype;
    private String fpMethodFollowup;
    private String fpSideeffectsOther;
    private String fpMethodFollowupOther;
    private String fpSideeffects;
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
    private String visitFlowStatusFlag;

    @Id
    @Column(name = "BenVisitID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getBenVisitId() {
        return benVisitId;
    }

    public void setBenVisitId(long benVisitId) {
        this.benVisitId = benVisitId;
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
    @Column(name = "VisitCode")
    public Long getVisitCode() {
        return visitCode;
    }

    public void setVisitCode(Long visitCode) {
        this.visitCode = visitCode;
    }

    @Basic
    @Column(name = "VisitDateTime")
    public Timestamp getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(Timestamp visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    @Basic
    @Column(name = "VisitNo")
    public Short getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(Short visitNo) {
        this.visitNo = visitNo;
    }

    @Basic
    @Column(name = "VisitReason")
    public String getVisitReason() {
        return visitReason;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    @Basic
    @Column(name = "VisitCategory")
    public String getVisitCategory() {
        return visitCategory;
    }

    public void setVisitCategory(String visitCategory) {
        this.visitCategory = visitCategory;
    }

    @Basic
    @Column(name = "SubVisitCategory")
    public String getSubVisitCategory() {
        return subVisitCategory;
    }

    public void setSubVisitCategory(String subVisitCategory) {
        this.subVisitCategory = subVisitCategory;
    }

    @Basic
    @Column(name = "PregnancyStatus")
    public String getPregnancyStatus() {
        return pregnancyStatus;
    }

    public void setPregnancyStatus(String pregnancyStatus) {
        this.pregnancyStatus = pregnancyStatus;
    }

    @Basic
    @Column(name = "RCHID")
    public String getRchid() {
        return rchid;
    }

    public void setRchid(String rchid) {
        this.rchid = rchid;
    }

    @Basic
    @Column(name = "HealthFacilityType")
    public String getHealthFacilityType() {
        return healthFacilityType;
    }

    public void setHealthFacilityType(String healthFacilityType) {
        this.healthFacilityType = healthFacilityType;
    }

    @Basic
    @Column(name = "HealthFacilityLocation")
    public String getHealthFacilityLocation() {
        return healthFacilityLocation;
    }

    public void setHealthFacilityLocation(String healthFacilityLocation) {
        this.healthFacilityLocation = healthFacilityLocation;
    }

    @Basic
    @Column(name = "ReportFilePath")
    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    @Basic
    @Column(name = "HealthID")
    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    @Basic
    @Column(name = "HealthIdNumber")
    public String getHealthIdNumber() {
        return healthIdNumber;
    }

    public void setHealthIdNumber(String healthIdNumber) {
        this.healthIdNumber = healthIdNumber;
    }

    @Basic
    @Column(name = "CarecontextLinkDate")
    public Timestamp getCarecontextLinkDate() {
        return carecontextLinkDate;
    }

    public void setCarecontextLinkDate(Timestamp carecontextLinkDate) {
        this.carecontextLinkDate = carecontextLinkDate;
    }

    @Basic
    @Column(name = "screeningtype")
    public String getScreeningtype() {
        return screeningtype;
    }

    public void setScreeningtype(String screeningtype) {
        this.screeningtype = screeningtype;
    }

    @Basic
    @Column(name = "FPMethod_Followup")
    public String getFpMethodFollowup() {
        return fpMethodFollowup;
    }

    public void setFpMethodFollowup(String fpMethodFollowup) {
        this.fpMethodFollowup = fpMethodFollowup;
    }

    @Basic
    @Column(name = "FP_Sideeffects_Other")
    public String getFpSideeffectsOther() {
        return fpSideeffectsOther;
    }

    public void setFpSideeffectsOther(String fpSideeffectsOther) {
        this.fpSideeffectsOther = fpSideeffectsOther;
    }

    @Basic
    @Column(name = "FPMethod_Followup_Other")
    public String getFpMethodFollowupOther() {
        return fpMethodFollowupOther;
    }

    public void setFpMethodFollowupOther(String fpMethodFollowupOther) {
        this.fpMethodFollowupOther = fpMethodFollowupOther;
    }

    @Basic
    @Column(name = "FP_Sideeffects")
    public String getFpSideeffects() {
        return fpSideeffects;
    }

    public void setFpSideeffects(String fpSideeffects) {
        this.fpSideeffects = fpSideeffects;
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

    @Basic
    @Column(name = "VisitFlowStatusFlag")
    public String getVisitFlowStatusFlag() {
        return visitFlowStatusFlag;
    }

    public void setVisitFlowStatusFlag(String visitFlowStatusFlag) {
        this.visitFlowStatusFlag = visitFlowStatusFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BenVisitDetail that = (BenVisitDetail) o;
        return benVisitId == that.benVisitId &&
                Objects.equals(beneficiaryRegId, that.beneficiaryRegId) &&
                Objects.equals(visitCode, that.visitCode) &&
                Objects.equals(visitDateTime, that.visitDateTime) &&
                Objects.equals(visitNo, that.visitNo) &&
                Objects.equals(visitReason, that.visitReason) &&
                Objects.equals(visitCategory, that.visitCategory) &&
                Objects.equals(subVisitCategory, that.subVisitCategory) &&
                Objects.equals(pregnancyStatus, that.pregnancyStatus) &&
                Objects.equals(rchid, that.rchid) &&
                Objects.equals(healthFacilityType, that.healthFacilityType) &&
                Objects.equals(healthFacilityLocation, that.healthFacilityLocation) &&
                Objects.equals(reportFilePath, that.reportFilePath) &&
                Objects.equals(healthId, that.healthId) &&
                Objects.equals(healthIdNumber, that.healthIdNumber) &&
                Objects.equals(carecontextLinkDate, that.carecontextLinkDate) &&
                Objects.equals(screeningtype, that.screeningtype) &&
                Objects.equals(fpMethodFollowup, that.fpMethodFollowup) &&
                Objects.equals(fpSideeffectsOther, that.fpSideeffectsOther) &&
                Objects.equals(fpMethodFollowupOther, that.fpMethodFollowupOther) &&
                Objects.equals(fpSideeffects, that.fpSideeffects) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(processed, that.processed) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(modifiedBy, that.modifiedBy) &&
                Objects.equals(lastModDate, that.lastModDate) &&
                Objects.equals(vanSerialNo, that.vanSerialNo) &&
                Objects.equals(vanId, that.vanId) &&
                Objects.equals(vehicalNo, that.vehicalNo) &&
                Objects.equals(parkingPlaceId, that.parkingPlaceId) &&
                Objects.equals(syncedBy, that.syncedBy) &&
                Objects.equals(syncedDate, that.syncedDate) &&
                Objects.equals(reservedForChange, that.reservedForChange) &&
                Objects.equals(visitFlowStatusFlag, that.visitFlowStatusFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(benVisitId, beneficiaryRegId, visitCode, visitDateTime, visitNo, visitReason, visitCategory, subVisitCategory, pregnancyStatus, rchid, healthFacilityType, healthFacilityLocation, reportFilePath, healthId, healthIdNumber, carecontextLinkDate, screeningtype, fpMethodFollowup, fpSideeffectsOther, fpMethodFollowupOther, fpSideeffects, deleted, processed, createdBy, createdDate, modifiedBy, lastModDate, vanSerialNo, vanId, vehicalNo, parkingPlaceId, syncedBy, syncedDate, reservedForChange, visitFlowStatusFlag);
    }
}
