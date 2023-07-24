package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TBScreeningDTO {

    private Long id;

    private Long benId;

    private Timestamp visitDate;

    private Boolean coughMoreThan2Weeks;

    private Boolean bloodInSputum;

    private Boolean feverMoreThan2Weeks;

    private Boolean lossOfWeight;

    private Boolean nightSweats;

    private Boolean historyOfTb;

    private Boolean takingAntiTBDrugs;

    private Boolean familySufferingFromTB;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBenId() {
        return benId;
    }

    public void setBenId(Long benId) {
        this.benId = benId;
    }

    public Timestamp getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }

    public Boolean getCoughMoreThan2Weeks() {
        return coughMoreThan2Weeks;
    }

    public void setCoughMoreThan2Weeks(Boolean coughMoreThan2Weeks) {
        this.coughMoreThan2Weeks = coughMoreThan2Weeks;
    }

    public Boolean getBloodInSputum() {
        return bloodInSputum;
    }

    public void setBloodInSputum(Boolean bloodInSputum) {
        this.bloodInSputum = bloodInSputum;
    }

    public Boolean getFeverMoreThan2Weeks() {
        return feverMoreThan2Weeks;
    }

    public void setFeverMoreThan2Weeks(Boolean feverMoreThan2Weeks) {
        this.feverMoreThan2Weeks = feverMoreThan2Weeks;
    }

    public Boolean getLossOfWeight() {
        return lossOfWeight;
    }

    public void setLossOfWeight(Boolean lossOfWeight) {
        this.lossOfWeight = lossOfWeight;
    }

    public Boolean getNightSweats() {
        return nightSweats;
    }

    public void setNightSweats(Boolean nightSweats) {
        this.nightSweats = nightSweats;
    }

    public Boolean getHistoryOfTb() {
        return historyOfTb;
    }

    public void setHistoryOfTb(Boolean historyOfTb) {
        this.historyOfTb = historyOfTb;
    }

    public Boolean getTakingAntiTBDrugs() {
        return takingAntiTBDrugs;
    }

    public void setTakingAntiTBDrugs(Boolean takingAntiTBDrugs) {
        this.takingAntiTBDrugs = takingAntiTBDrugs;
    }

    public Boolean getFamilySufferingFromTB() {
        return familySufferingFromTB;
    }

    public void setFamilySufferingFromTB(Boolean familySufferingFromTB) {
        this.familySufferingFromTB = familySufferingFromTB;
    }
}
