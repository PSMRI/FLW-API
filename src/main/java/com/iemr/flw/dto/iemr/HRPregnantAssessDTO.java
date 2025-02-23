package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HRPregnantAssessDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private Timestamp visitDate;

    private String noOfDeliveries;

    private String timeLessThan18m;

    private String heightShort;

    private String age;

    private String rhNegative;

    private String homeDelivery;

    private String badObstetric;

    private String multiplePregnancy;

    private Boolean isHighRisk;

    private Timestamp lmpDate;

    private Timestamp edd;

    public Timestamp getLmpDate() {
        return lmpDate;
    }

    public void setLmpDate(Timestamp lmpDate) {
        this.lmpDate = lmpDate;
    }

    public Timestamp getEdd() {
        return edd;
    }

    public void setEdd(Timestamp edd) {
        this.edd = edd;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getBenId() {
        return benId;
    }

    public void setBenId(Long benId) {
        this.benId = benId;
    }

    public String getNoOfDeliveries() {
        return noOfDeliveries;
    }

    public void setNoOfDeliveries(String noOfDeliveries) {
        this.noOfDeliveries = noOfDeliveries;
    }

    public String getTimeLessThan18m() {
        return timeLessThan18m;
    }

    public void setTimeLessThan18m(String timeLessThan18m) {
        this.timeLessThan18m = timeLessThan18m;
    }

    public String getHeightShort() {
        return heightShort;
    }

    public void setHeightShort(String heightShort) {
        this.heightShort = heightShort;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRhNegative() {
        return rhNegative;
    }

    public void setRhNegative(String rhNegative) {
        this.rhNegative = rhNegative;
    }

    public String getBadObstetric() {
        return badObstetric;
    }

    public void setBadObstetric(String badObstetric) {
        this.badObstetric = badObstetric;
    }

    public String getMultiplePregnancy() {
        return multiplePregnancy;
    }

    public void setMultiplePregnancy(String multiplePregnancy) {
        this.multiplePregnancy = multiplePregnancy;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public Boolean getHighRisk() {
        return isHighRisk;
    }

    public void setHighRisk(Boolean highRisk) {
        isHighRisk = highRisk;
    }

    public Timestamp getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }
}

