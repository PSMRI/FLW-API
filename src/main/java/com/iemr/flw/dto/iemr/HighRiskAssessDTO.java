package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HighRiskAssessDTO {
    private Long id;

    private Integer userId;

    private Long benId;

    private String noOfDeliveries;

    private String timeLessThan18m;

    private String heightShort;

    private String age;

    private Timestamp createdDate;

    private String createdBy;

    private Timestamp updatedDate;

    private String updatedBy;

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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
