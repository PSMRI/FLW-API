package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HRPregnantAssessDTO {

    private Long id;

    private Integer userId;

    private Long benId;

    private String noOfDeliveries;

    private String timeLessThan18m;

    private String heightShort;

    private String age;

    private String misCarriage;

    private String homeDelivery;

    private String medicalIssues;

    private String pastCSection;

    private Boolean isHighRisk;

    private Timestamp visitDate;

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

    public String getMisCarriage() {
        return misCarriage;
    }

    public void setMisCarriage(String misCarriage) {
        this.misCarriage = misCarriage;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public String getMedicalIssues() {
        return medicalIssues;
    }

    public void setMedicalIssues(String medicalIssues) {
        this.medicalIssues = medicalIssues;
    }

    public String getPastCSection() {
        return pastCSection;
    }

    public void setPastCSection(String pastCSection) {
        this.pastCSection = pastCSection;
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

