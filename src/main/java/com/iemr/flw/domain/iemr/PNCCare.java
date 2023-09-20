package com.iemr.flw.domain.iemr;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "t_pnccare", schema = "db_iemr", catalog = "")
public class PNCCare {
    private long id;
    private Long beneficiaryRegId;
    private Long benVisitId;
    private Short visitNo;
    private Short deliveryTypeId;
    private String deliveryType;
    private Short deliveryPlaceId;
    private String deliveryPlace;
    private String otherDeliveryPlace;
    private Timestamp dateOfDelivery;
    private Short deliveryComplicationId;
    private String deliveryComplication;
    private String otherDeliveryComplication;
    private Short postpartumComplicationId;
    private String postpartumComplication;
    private Short pregOutcomeId;
    private String pregOutcome;
    private Short postNatalComplicationId;
    private String postNatalComplication;
    private String otherPostNatalComplication;
    private Short gestationId;
    private String gestationName;
    private String gestationalAgeOfNewborn;
    private Integer birthWeightOfNewborn;
    private Integer newBornHealthStatusId;
    private String newBornHealthStatus;
    private String deliveryconductedby;
    private Integer deliveryConductedById;
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

    @Id
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
    @Column(name = "VisitNo")
    public Short getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(Short visitNo) {
        this.visitNo = visitNo;
    }

    @Basic
    @Column(name = "DeliveryTypeID")
    public Short getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(Short deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    @Basic
    @Column(name = "DeliveryType")
    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    @Basic
    @Column(name = "DeliveryPlaceID")
    public Short getDeliveryPlaceId() {
        return deliveryPlaceId;
    }

    public void setDeliveryPlaceId(Short deliveryPlaceId) {
        this.deliveryPlaceId = deliveryPlaceId;
    }

    @Basic
    @Column(name = "DeliveryPlace")
    public String getDeliveryPlace() {
        return deliveryPlace;
    }

    public void setDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }

    @Basic
    @Column(name = "OtherDeliveryPlace")
    public String getOtherDeliveryPlace() {
        return otherDeliveryPlace;
    }

    public void setOtherDeliveryPlace(String otherDeliveryPlace) {
        this.otherDeliveryPlace = otherDeliveryPlace;
    }

    @Basic
    @Column(name = "DateOfDelivery")
    public Timestamp getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(Timestamp dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    @Basic
    @Column(name = "DeliveryComplicationID")
    public Short getDeliveryComplicationId() {
        return deliveryComplicationId;
    }

    public void setDeliveryComplicationId(Short deliveryComplicationId) {
        this.deliveryComplicationId = deliveryComplicationId;
    }

    @Basic
    @Column(name = "DeliveryComplication")
    public String getDeliveryComplication() {
        return deliveryComplication;
    }

    public void setDeliveryComplication(String deliveryComplication) {
        this.deliveryComplication = deliveryComplication;
    }

    @Basic
    @Column(name = "OtherDeliveryComplication")
    public String getOtherDeliveryComplication() {
        return otherDeliveryComplication;
    }

    public void setOtherDeliveryComplication(String otherDeliveryComplication) {
        this.otherDeliveryComplication = otherDeliveryComplication;
    }

    @Basic
    @Column(name = "PostpartumComplicationID")
    public Short getPostpartumComplicationId() {
        return postpartumComplicationId;
    }

    public void setPostpartumComplicationId(Short postpartumComplicationId) {
        this.postpartumComplicationId = postpartumComplicationId;
    }

    @Basic
    @Column(name = "PostpartumComplication")
    public String getPostpartumComplication() {
        return postpartumComplication;
    }

    public void setPostpartumComplication(String postpartumComplication) {
        this.postpartumComplication = postpartumComplication;
    }

    @Basic
    @Column(name = "PregOutcomeID")
    public Short getPregOutcomeId() {
        return pregOutcomeId;
    }

    public void setPregOutcomeId(Short pregOutcomeId) {
        this.pregOutcomeId = pregOutcomeId;
    }

    @Basic
    @Column(name = "PregOutcome")
    public String getPregOutcome() {
        return pregOutcome;
    }

    public void setPregOutcome(String pregOutcome) {
        this.pregOutcome = pregOutcome;
    }

    @Basic
    @Column(name = "PostNatalComplicationID")
    public Short getPostNatalComplicationId() {
        return postNatalComplicationId;
    }

    public void setPostNatalComplicationId(Short postNatalComplicationId) {
        this.postNatalComplicationId = postNatalComplicationId;
    }

    @Basic
    @Column(name = "PostNatalComplication")
    public String getPostNatalComplication() {
        return postNatalComplication;
    }

    public void setPostNatalComplication(String postNatalComplication) {
        this.postNatalComplication = postNatalComplication;
    }

    @Basic
    @Column(name = "OtherPostNatalComplication")
    public String getOtherPostNatalComplication() {
        return otherPostNatalComplication;
    }

    public void setOtherPostNatalComplication(String otherPostNatalComplication) {
        this.otherPostNatalComplication = otherPostNatalComplication;
    }

    @Basic
    @Column(name = "GestationID")
    public Short getGestationId() {
        return gestationId;
    }

    public void setGestationId(Short gestationId) {
        this.gestationId = gestationId;
    }

    @Basic
    @Column(name = "GestationName")
    public String getGestationName() {
        return gestationName;
    }

    public void setGestationName(String gestationName) {
        this.gestationName = gestationName;
    }

    @Basic
    @Column(name = "GestationalAgeOfNewborn")
    public String getGestationalAgeOfNewborn() {
        return gestationalAgeOfNewborn;
    }

    public void setGestationalAgeOfNewborn(String gestationalAgeOfNewborn) {
        this.gestationalAgeOfNewborn = gestationalAgeOfNewborn;
    }

    @Basic
    @Column(name = "BirthWeightOfNewborn")
    public Integer getBirthWeightOfNewborn() {
        return birthWeightOfNewborn;
    }

    public void setBirthWeightOfNewborn(Integer birthWeightOfNewborn) {
        this.birthWeightOfNewborn = birthWeightOfNewborn;
    }

    @Basic
    @Column(name = "NewBornHealthStatusID")
    public Integer getNewBornHealthStatusId() {
        return newBornHealthStatusId;
    }

    public void setNewBornHealthStatusId(Integer newBornHealthStatusId) {
        this.newBornHealthStatusId = newBornHealthStatusId;
    }

    @Basic
    @Column(name = "NewBornHealthStatus")
    public String getNewBornHealthStatus() {
        return newBornHealthStatus;
    }

    public void setNewBornHealthStatus(String newBornHealthStatus) {
        this.newBornHealthStatus = newBornHealthStatus;
    }

    @Basic
    @Column(name = "deliveryconductedby")
    public String getDeliveryconductedby() {
        return deliveryconductedby;
    }

    public void setDeliveryconductedby(String deliveryconductedby) {
        this.deliveryconductedby = deliveryconductedby;
    }

    @Basic
    @Column(name = "DeliveryConductedById")
    public Integer getDeliveryConductedById() {
        return deliveryConductedById;
    }

    public void setDeliveryConductedById(Integer deliveryConductedById) {
        this.deliveryConductedById = deliveryConductedById;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PNCCare PNCCare = (PNCCare) o;
        return id == PNCCare.id &&
                Objects.equals(beneficiaryRegId, PNCCare.beneficiaryRegId) &&
                Objects.equals(benVisitId, PNCCare.benVisitId) &&
                Objects.equals(visitNo, PNCCare.visitNo) &&
                Objects.equals(deliveryTypeId, PNCCare.deliveryTypeId) &&
                Objects.equals(deliveryType, PNCCare.deliveryType) &&
                Objects.equals(deliveryPlaceId, PNCCare.deliveryPlaceId) &&
                Objects.equals(deliveryPlace, PNCCare.deliveryPlace) &&
                Objects.equals(otherDeliveryPlace, PNCCare.otherDeliveryPlace) &&
                Objects.equals(dateOfDelivery, PNCCare.dateOfDelivery) &&
                Objects.equals(deliveryComplicationId, PNCCare.deliveryComplicationId) &&
                Objects.equals(deliveryComplication, PNCCare.deliveryComplication) &&
                Objects.equals(otherDeliveryComplication, PNCCare.otherDeliveryComplication) &&
                Objects.equals(postpartumComplicationId, PNCCare.postpartumComplicationId) &&
                Objects.equals(postpartumComplication, PNCCare.postpartumComplication) &&
                Objects.equals(pregOutcomeId, PNCCare.pregOutcomeId) &&
                Objects.equals(pregOutcome, PNCCare.pregOutcome) &&
                Objects.equals(postNatalComplicationId, PNCCare.postNatalComplicationId) &&
                Objects.equals(postNatalComplication, PNCCare.postNatalComplication) &&
                Objects.equals(otherPostNatalComplication, PNCCare.otherPostNatalComplication) &&
                Objects.equals(gestationId, PNCCare.gestationId) &&
                Objects.equals(gestationName, PNCCare.gestationName) &&
                Objects.equals(gestationalAgeOfNewborn, PNCCare.gestationalAgeOfNewborn) &&
                Objects.equals(birthWeightOfNewborn, PNCCare.birthWeightOfNewborn) &&
                Objects.equals(newBornHealthStatusId, PNCCare.newBornHealthStatusId) &&
                Objects.equals(newBornHealthStatus, PNCCare.newBornHealthStatus) &&
                Objects.equals(deliveryconductedby, PNCCare.deliveryconductedby) &&
                Objects.equals(deliveryConductedById, PNCCare.deliveryConductedById) &&
                Objects.equals(deleted, PNCCare.deleted) &&
                Objects.equals(processed, PNCCare.processed) &&
                Objects.equals(createdBy, PNCCare.createdBy) &&
                Objects.equals(createdDate, PNCCare.createdDate) &&
                Objects.equals(modifiedBy, PNCCare.modifiedBy) &&
                Objects.equals(lastModDate, PNCCare.lastModDate) &&
                Objects.equals(vanSerialNo, PNCCare.vanSerialNo) &&
                Objects.equals(vanId, PNCCare.vanId) &&
                Objects.equals(vehicalNo, PNCCare.vehicalNo) &&
                Objects.equals(parkingPlaceId, PNCCare.parkingPlaceId) &&
                Objects.equals(syncedBy, PNCCare.syncedBy) &&
                Objects.equals(syncedDate, PNCCare.syncedDate) &&
                Objects.equals(reservedForChange, PNCCare.reservedForChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, beneficiaryRegId, benVisitId, visitNo, deliveryTypeId, deliveryType, deliveryPlaceId, deliveryPlace, otherDeliveryPlace, dateOfDelivery, deliveryComplicationId, deliveryComplication, otherDeliveryComplication, postpartumComplicationId, postpartumComplication, pregOutcomeId, pregOutcome, postNatalComplicationId, postNatalComplication, otherPostNatalComplication, gestationId, gestationName, gestationalAgeOfNewborn, birthWeightOfNewborn, newBornHealthStatusId, newBornHealthStatus, deliveryconductedby, deliveryConductedById, deleted, processed, createdBy, createdDate, modifiedBy, lastModDate, vanSerialNo, vanId, vehicalNo, parkingPlaceId, syncedBy, syncedDate, reservedForChange);
    }
}
