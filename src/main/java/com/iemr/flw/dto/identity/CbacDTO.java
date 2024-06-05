package com.iemr.flw.dto.identity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
public class CbacDTO {
    private Long cbacDetailsId;
    private Long beneficiaryRegId;
    private Long beneficiaryId;
    private Timestamp filledDate;
    private String cbacAge;
    private Integer cbacAgePosi;
    private String cbacAlcohol;
    private Integer cbacAlcoholPosi;
    private String cbacAntitbdrugs;
    private Integer cbacAntitbdrugsPos;
    private String cbacBleedingafterintercourse;
    private Integer cbacBleedingafterintercoursePos;
    private String cbacBleedingaftermenopause;
    private Integer cbacBleedingaftermenopausePos;
    private String cbacBleedingbtwnperiods;
    private Integer cbacBleedingbtwnperiodsPos;
    private String cbacBlooddischage;
    private Integer cbacBlooddischagePos;
    private String cbacBloodsputum;
    private Integer cbacBloodsputumPos;
    private String cbacChangeinbreast;
    private Integer cbacChangeinbreastPos;
    private String cbacCoughing;
    private Integer cbacCoughingPos;
    private String cbacDifficultyinmouth;
    private Integer cbacDifficultyinmouthPos;
    private String cbacFamilyhistory;
    private Integer cbacFamilyhistoryPosi;
    private String cbacFivermore;
    private Integer cbacFivermorePos;
    private String cbacFoulveginaldischarge;
    private Integer cbacFoulveginaldischargePos;
    private String cbacHistoryoffits;
    private Integer cbacHistoryoffitsPos;
    private String cbacLoseofweight;
    private Integer cbacLoseofweightPos;
    private String cbacLumpinbreast;
    private Integer cbacLumpinbreastPos;
    private String cbacNightsweats;
    private Integer cbacNightsweatsPos;
    private String cbacPa;
    private Integer cbacPaPosi;
    private Integer cbacReferpatientMo;
    private String cbacSmoke;
    private Integer cbacSmokePosi;
    private String cbacSortnesofbirth;
    private Integer cbacSortnesofbirthPos;
    private Integer cbacSputemcollection;
    private String cbacSufferingtb;
    private Integer cbacSufferingtbPos;
    private String cbacTbhistory;
    private Integer cbacTbhistoryPos;
    private String cbacToneofvoice;
    private Integer cbacToneofvoicePos;
    private String cbacTracingAllFm;
    private String cbacUicers;
    private Integer cbacUicersPos;
    private String cbacWaist;
    private Integer cbacWaistPosi;
    private Long houseoldId;
    private Integer countryid;
    private Integer stateid;
    private Integer districtid;
    private String districtname;
    private Integer villageid;
    private Integer serverUpdatedStatus;
    private Integer totalScore;
    private String cbacPainWhileChewing;
    private Integer cbacPainWhileChewingPosi;
    private String cbacAnyThickendSkin;
    private Integer cbacAnyThickendSkinPosi;
    private String cbacClawingOfFingers;
    private Integer cbacClawingOfFingersPosi;
    private String cbacDiffHoldingObj;
    private Integer cbacDiffHoldingObjPosi;
    private String cbacFeelingDown;
    private Integer cbacFeelingDownPosi;
    private Integer cbacFeelingDownScore;
    private String cbacFuelUsed;
    private Integer cbacFuelUsedPosi;
    private String cbacGrowthInMouth;
    private Integer cbacGrowthInMouthPosi;
    private String cbacHyperPigmentedPatch;
    private Integer cbacHyperPigmentedPatchPosi;
    private String cbacInabilityCloseEyelid;
    private Integer cbacInabilityCloseEyelidPosi;
    private String cbacLittleInterest;
    private Integer cbacLittleInterestPosi;
    private Integer cbacLittleInterestScore;
    private String cbacNodulesOnSkin;
    private Integer cbacNodulesOnSkinPosi;
    private String cbacNumbnessOnPalm;
    private Integer cbacNumbnessOnPalmPosi;
    private String cbacOccupationalExposure;
    private Integer cbacOccupationalExposurePosi;
    private String cbacTinglingOrNumbness;
    private Integer cbacTinglingOrNumbnessPosi;
    private String cbacWeeknessInFeet;
    private Integer cbacWeeknessInFeetPosi;
    private String suspectedHrp;
    private String suspectedNcd;
    private String suspectedTb;
    private String suspectedNcdDiseases;
    private String confirmedNcd;
    private String confirmedHrp;
    private String confirmedTb;
    private String confirmedNcdDiseases;
    private String diagnosisStatus;
    private Boolean deleted;
    private String processed;
    private String createdBy;
    private Timestamp createdDate;
    private Boolean reserved;
    private String reservedFor;
    private String reservedOn;
    private Integer reservedById;
    private String modifiedBy;
    private Timestamp lastModDate;
    private Long vanSerialNo;
    private Integer vanId;
    private String vehicalNo;
    private Integer parkingPlaceId;
    private String syncedBy;
    private Timestamp syncedDate;
    private Integer providerServiceMapId;
    private Integer deviceId;
    private String cbacCloudy;
    private Integer cbacCloudyPosi;
    private String cbacDiffreading;
    private Integer cbacDiffreadingPosi;
    private String cbacPainIneyes;
    private Integer cbacPainIneyesPosi;
    private String cbacRednessIneyes;
    private Integer cbacRednessIneyesPosi;
    private String cbacDiffInhearing;
    private Integer cbacDiffInhearingPosi;
    private String cbacFeelingUnsteady;
    private Integer cbacFeelingUnsteadyPosi;
    private String cbacSufferPhysicalDisability;
    private Integer cbacSufferPhysicalDisabilityPosi;
    private String cbacNeedingHelp;
    private Integer cbacNeedingHelpPosi;
    private String cbacForgettingNames;
    private Integer cbacForgettingNamesPosi;
    private String cbacTinglingPalm;
    private Integer cbacTinglingPalmPosi;
    private String cbacWhiteOrRedPatch;
    private Integer cbacWhiteOrRedPatchPosi;

    public Long getCbacDetailsId() {
        return cbacDetailsId;
    }

    public void setCbacDetailsId(Long cbacDetailsId) {
        this.cbacDetailsId = cbacDetailsId;
    }

    public Long getBeneficiaryRegId() {
        return beneficiaryRegId;
    }

    public void setBeneficiaryRegId(Long beneficiaryRegId) {
        this.beneficiaryRegId = beneficiaryRegId;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public Timestamp getFilledDate() {
        return filledDate;
    }

    public void setFilledDate(Timestamp filledDate) {
        this.filledDate = filledDate;
    }

    public String getCbacAge() {
        return cbacAge;
    }

    public void setCbacAge(String cbacAge) {
        this.cbacAge = cbacAge;
    }

    public Integer getCbacAgePosi() {
        return cbacAgePosi;
    }

    public void setCbacAgePosi(Integer cbacAgePosi) {
        this.cbacAgePosi = cbacAgePosi;
    }

    public String getCbacAlcohol() {
        return cbacAlcohol;
    }

    public void setCbacAlcohol(String cbacAlcohol) {
        this.cbacAlcohol = cbacAlcohol;
    }

    public Integer getCbacAlcoholPosi() {
        return cbacAlcoholPosi;
    }

    public void setCbacAlcoholPosi(Integer cbacAlcoholPosi) {
        this.cbacAlcoholPosi = cbacAlcoholPosi;
    }

    public String getCbacAntitbdrugs() {
        return cbacAntitbdrugs;
    }

    public void setCbacAntitbdrugs(String cbacAntitbdrugs) {
        this.cbacAntitbdrugs = cbacAntitbdrugs;
    }

    public Integer getCbacAntitbdrugsPos() {
        return cbacAntitbdrugsPos;
    }

    public void setCbacAntitbdrugsPos(Integer cbacAntitbdrugsPos) {
        this.cbacAntitbdrugsPos = cbacAntitbdrugsPos;
    }

    public String getCbacBleedingafterintercourse() {
        return cbacBleedingafterintercourse;
    }

    public void setCbacBleedingafterintercourse(String cbacBleedingafterintercourse) {
        this.cbacBleedingafterintercourse = cbacBleedingafterintercourse;
    }

    public Integer getCbacBleedingafterintercoursePos() {
        return cbacBleedingafterintercoursePos;
    }

    public void setCbacBleedingafterintercoursePos(Integer cbacBleedingafterintercoursePos) {
        this.cbacBleedingafterintercoursePos = cbacBleedingafterintercoursePos;
    }

    public String getCbacBleedingaftermenopause() {
        return cbacBleedingaftermenopause;
    }

    public void setCbacBleedingaftermenopause(String cbacBleedingaftermenopause) {
        this.cbacBleedingaftermenopause = cbacBleedingaftermenopause;
    }

    public Integer getCbacBleedingaftermenopausePos() {
        return cbacBleedingaftermenopausePos;
    }

    public void setCbacBleedingaftermenopausePos(Integer cbacBleedingaftermenopausePos) {
        this.cbacBleedingaftermenopausePos = cbacBleedingaftermenopausePos;
    }

    public String getCbacBleedingbtwnperiods() {
        return cbacBleedingbtwnperiods;
    }

    public void setCbacBleedingbtwnperiods(String cbacBleedingbtwnperiods) {
        this.cbacBleedingbtwnperiods = cbacBleedingbtwnperiods;
    }

    public Integer getCbacBleedingbtwnperiodsPos() {
        return cbacBleedingbtwnperiodsPos;
    }

    public void setCbacBleedingbtwnperiodsPos(Integer cbacBleedingbtwnperiodsPos) {
        this.cbacBleedingbtwnperiodsPos = cbacBleedingbtwnperiodsPos;
    }

    public String getCbacBlooddischage() {
        return cbacBlooddischage;
    }

    public void setCbacBlooddischage(String cbacBlooddischage) {
        this.cbacBlooddischage = cbacBlooddischage;
    }

    public Integer getCbacBlooddischagePos() {
        return cbacBlooddischagePos;
    }

    public void setCbacBlooddischagePos(Integer cbacBlooddischagePos) {
        this.cbacBlooddischagePos = cbacBlooddischagePos;
    }

    public String getCbacBloodsputum() {
        return cbacBloodsputum;
    }

    public void setCbacBloodsputum(String cbacBloodsputum) {
        this.cbacBloodsputum = cbacBloodsputum;
    }

    public Integer getCbacBloodsputumPos() {
        return cbacBloodsputumPos;
    }

    public void setCbacBloodsputumPos(Integer cbacBloodsputumPos) {
        this.cbacBloodsputumPos = cbacBloodsputumPos;
    }

    public String getCbacChangeinbreast() {
        return cbacChangeinbreast;
    }

    public void setCbacChangeinbreast(String cbacChangeinbreast) {
        this.cbacChangeinbreast = cbacChangeinbreast;
    }

    public Integer getCbacChangeinbreastPos() {
        return cbacChangeinbreastPos;
    }

    public void setCbacChangeinbreastPos(Integer cbacChangeinbreastPos) {
        this.cbacChangeinbreastPos = cbacChangeinbreastPos;
    }

    public String getCbacCoughing() {
        return cbacCoughing;
    }

    public void setCbacCoughing(String cbacCoughing) {
        this.cbacCoughing = cbacCoughing;
    }

    public Integer getCbacCoughingPos() {
        return cbacCoughingPos;
    }

    public void setCbacCoughingPos(Integer cbacCoughingPos) {
        this.cbacCoughingPos = cbacCoughingPos;
    }

    public String getCbacDifficultyinmouth() {
        return cbacDifficultyinmouth;
    }

    public void setCbacDifficultyinmouth(String cbacDifficultyinmouth) {
        this.cbacDifficultyinmouth = cbacDifficultyinmouth;
    }

    public Integer getCbacDifficultyinmouthPos() {
        return cbacDifficultyinmouthPos;
    }

    public void setCbacDifficultyinmouthPos(Integer cbacDifficultyinmouthPos) {
        this.cbacDifficultyinmouthPos = cbacDifficultyinmouthPos;
    }

    public String getCbacFamilyhistory() {
        return cbacFamilyhistory;
    }

    public void setCbacFamilyhistory(String cbacFamilyhistory) {
        this.cbacFamilyhistory = cbacFamilyhistory;
    }

    public Integer getCbacFamilyhistoryPosi() {
        return cbacFamilyhistoryPosi;
    }

    public void setCbacFamilyhistoryPosi(Integer cbacFamilyhistoryPosi) {
        this.cbacFamilyhistoryPosi = cbacFamilyhistoryPosi;
    }

    public String getCbacFivermore() {
        return cbacFivermore;
    }

    public void setCbacFivermore(String cbacFivermore) {
        this.cbacFivermore = cbacFivermore;
    }

    public Integer getCbacFivermorePos() {
        return cbacFivermorePos;
    }

    public void setCbacFivermorePos(Integer cbacFivermorePos) {
        this.cbacFivermorePos = cbacFivermorePos;
    }

    public String getCbacFoulveginaldischarge() {
        return cbacFoulveginaldischarge;
    }

    public void setCbacFoulveginaldischarge(String cbacFoulveginaldischarge) {
        this.cbacFoulveginaldischarge = cbacFoulveginaldischarge;
    }

    public Integer getCbacFoulveginaldischargePos() {
        return cbacFoulveginaldischargePos;
    }

    public void setCbacFoulveginaldischargePos(Integer cbacFoulveginaldischargePos) {
        this.cbacFoulveginaldischargePos = cbacFoulveginaldischargePos;
    }

    public String getCbacHistoryoffits() {
        return cbacHistoryoffits;
    }

    public void setCbacHistoryoffits(String cbacHistoryoffits) {
        this.cbacHistoryoffits = cbacHistoryoffits;
    }

    public Integer getCbacHistoryoffitsPos() {
        return cbacHistoryoffitsPos;
    }

    public void setCbacHistoryoffitsPos(Integer cbacHistoryoffitsPos) {
        this.cbacHistoryoffitsPos = cbacHistoryoffitsPos;
    }

    public String getCbacLoseofweight() {
        return cbacLoseofweight;
    }

    public void setCbacLoseofweight(String cbacLoseofweight) {
        this.cbacLoseofweight = cbacLoseofweight;
    }

    public Integer getCbacLoseofweightPos() {
        return cbacLoseofweightPos;
    }

    public void setCbacLoseofweightPos(Integer cbacLoseofweightPos) {
        this.cbacLoseofweightPos = cbacLoseofweightPos;
    }

    public String getCbacLumpinbreast() {
        return cbacLumpinbreast;
    }

    public void setCbacLumpinbreast(String cbacLumpinbreast) {
        this.cbacLumpinbreast = cbacLumpinbreast;
    }

    public Integer getCbacLumpinbreastPos() {
        return cbacLumpinbreastPos;
    }

    public void setCbacLumpinbreastPos(Integer cbacLumpinbreastPos) {
        this.cbacLumpinbreastPos = cbacLumpinbreastPos;
    }

    public String getCbacNightsweats() {
        return cbacNightsweats;
    }

    public void setCbacNightsweats(String cbacNightsweats) {
        this.cbacNightsweats = cbacNightsweats;
    }

    public Integer getCbacNightsweatsPos() {
        return cbacNightsweatsPos;
    }

    public void setCbacNightsweatsPos(Integer cbacNightsweatsPos) {
        this.cbacNightsweatsPos = cbacNightsweatsPos;
    }

    public String getCbacPa() {
        return cbacPa;
    }

    public void setCbacPa(String cbacPa) {
        this.cbacPa = cbacPa;
    }

    public Integer getCbacPaPosi() {
        return cbacPaPosi;
    }

    public void setCbacPaPosi(Integer cbacPaPosi) {
        this.cbacPaPosi = cbacPaPosi;
    }

    public Integer getCbacReferpatientMo() {
        return cbacReferpatientMo;
    }

    public void setCbacReferpatientMo(Integer cbacReferpatientMo) {
        this.cbacReferpatientMo = cbacReferpatientMo;
    }

    public String getCbacSmoke() {
        return cbacSmoke;
    }

    public void setCbacSmoke(String cbacSmoke) {
        this.cbacSmoke = cbacSmoke;
    }

    public Integer getCbacSmokePosi() {
        return cbacSmokePosi;
    }

    public void setCbacSmokePosi(Integer cbacSmokePosi) {
        this.cbacSmokePosi = cbacSmokePosi;
    }

    public String getCbacSortnesofbirth() {
        return cbacSortnesofbirth;
    }

    public void setCbacSortnesofbirth(String cbacSortnesofbirth) {
        this.cbacSortnesofbirth = cbacSortnesofbirth;
    }

    public Integer getCbacSortnesofbirthPos() {
        return cbacSortnesofbirthPos;
    }

    public void setCbacSortnesofbirthPos(Integer cbacSortnesofbirthPos) {
        this.cbacSortnesofbirthPos = cbacSortnesofbirthPos;
    }

    public Integer getCbacSputemcollection() {
        return cbacSputemcollection;
    }

    public void setCbacSputemcollection(Integer cbacSputemcollection) {
        this.cbacSputemcollection = cbacSputemcollection;
    }

    public String getCbacSufferingtb() {
        return cbacSufferingtb;
    }

    public void setCbacSufferingtb(String cbacSufferingtb) {
        this.cbacSufferingtb = cbacSufferingtb;
    }

    public Integer getCbacSufferingtbPos() {
        return cbacSufferingtbPos;
    }

    public void setCbacSufferingtbPos(Integer cbacSufferingtbPos) {
        this.cbacSufferingtbPos = cbacSufferingtbPos;
    }

    public String getCbacTbhistory() {
        return cbacTbhistory;
    }

    public void setCbacTbhistory(String cbacTbhistory) {
        this.cbacTbhistory = cbacTbhistory;
    }

    public Integer getCbacTbhistoryPos() {
        return cbacTbhistoryPos;
    }

    public void setCbacTbhistoryPos(Integer cbacTbhistoryPos) {
        this.cbacTbhistoryPos = cbacTbhistoryPos;
    }

    public String getCbacToneofvoice() {
        return cbacToneofvoice;
    }

    public void setCbacToneofvoice(String cbacToneofvoice) {
        this.cbacToneofvoice = cbacToneofvoice;
    }

    public Integer getCbacToneofvoicePos() {
        return cbacToneofvoicePos;
    }

    public void setCbacToneofvoicePos(Integer cbacToneofvoicePos) {
        this.cbacToneofvoicePos = cbacToneofvoicePos;
    }

    public String getCbacTracingAllFm() {
        return cbacTracingAllFm;
    }

    public void setCbacTracingAllFm(String cbacTracingAllFm) {
        this.cbacTracingAllFm = cbacTracingAllFm;
    }

    public String getCbacUicers() {
        return cbacUicers;
    }

    public void setCbacUicers(String cbacUicers) {
        this.cbacUicers = cbacUicers;
    }

    public Integer getCbacUicersPos() {
        return cbacUicersPos;
    }

    public void setCbacUicersPos(Integer cbacUicersPos) {
        this.cbacUicersPos = cbacUicersPos;
    }

    public String getCbacWaist() {
        return cbacWaist;
    }

    public void setCbacWaist(String cbacWaist) {
        this.cbacWaist = cbacWaist;
    }

    public Integer getCbacWaistPosi() {
        return cbacWaistPosi;
    }

    public void setCbacWaistPosi(Integer cbacWaistPosi) {
        this.cbacWaistPosi = cbacWaistPosi;
    }

    public Long getHouseoldId() {
        return houseoldId;
    }

    public void setHouseoldId(Long houseoldId) {
        this.houseoldId = houseoldId;
    }

    public Integer getCountryid() {
        return countryid;
    }

    public void setCountryid(Integer countryid) {
        this.countryid = countryid;
    }

    public Integer getStateid() {
        return stateid;
    }

    public void setStateid(Integer stateid) {
        this.stateid = stateid;
    }

    public Integer getDistrictid() {
        return districtid;
    }

    public void setDistrictid(Integer districtid) {
        this.districtid = districtid;
    }

    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }

    public Integer getVillageid() {
        return villageid;
    }

    public void setVillageid(Integer villageid) {
        this.villageid = villageid;
    }

    public Integer getServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(Integer serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getCbacPainWhileChewing() {
        return cbacPainWhileChewing;
    }

    public void setCbacPainWhileChewing(String cbacPainWhileChewing) {
        this.cbacPainWhileChewing = cbacPainWhileChewing;
    }

    public Integer getCbacPainWhileChewingPosi() {
        return cbacPainWhileChewingPosi;
    }

    public void setCbacPainWhileChewingPosi(Integer cbacPainWhileChewingPosi) {
        this.cbacPainWhileChewingPosi = cbacPainWhileChewingPosi;
    }

    public String getCbacAnyThickendSkin() {
        return cbacAnyThickendSkin;
    }

    public void setCbacAnyThickendSkin(String cbacAnyThickendSkin) {
        this.cbacAnyThickendSkin = cbacAnyThickendSkin;
    }

    public Integer getCbacAnyThickendSkinPosi() {
        return cbacAnyThickendSkinPosi;
    }

    public void setCbacAnyThickendSkinPosi(Integer cbacAnyThickendSkinPosi) {
        this.cbacAnyThickendSkinPosi = cbacAnyThickendSkinPosi;
    }

    public String getCbacClawingOfFingers() {
        return cbacClawingOfFingers;
    }

    public void setCbacClawingOfFingers(String cbacClawingOfFingers) {
        this.cbacClawingOfFingers = cbacClawingOfFingers;
    }

    public Integer getCbacClawingOfFingersPosi() {
        return cbacClawingOfFingersPosi;
    }

    public void setCbacClawingOfFingersPosi(Integer cbacClawingOfFingersPosi) {
        this.cbacClawingOfFingersPosi = cbacClawingOfFingersPosi;
    }

    public String getCbacDiffHoldingObj() {
        return cbacDiffHoldingObj;
    }

    public void setCbacDiffHoldingObj(String cbacDiffHoldingObj) {
        this.cbacDiffHoldingObj = cbacDiffHoldingObj;
    }

    public Integer getCbacDiffHoldingObjPosi() {
        return cbacDiffHoldingObjPosi;
    }

    public void setCbacDiffHoldingObjPosi(Integer cbacDiffHoldingObjPosi) {
        this.cbacDiffHoldingObjPosi = cbacDiffHoldingObjPosi;
    }

    public String getCbacFeelingDown() {
        return cbacFeelingDown;
    }

    public void setCbacFeelingDown(String cbacFeelingDown) {
        this.cbacFeelingDown = cbacFeelingDown;
    }

    public Integer getCbacFeelingDownPosi() {
        return cbacFeelingDownPosi;
    }

    public void setCbacFeelingDownPosi(Integer cbacFeelingDownPosi) {
        this.cbacFeelingDownPosi = cbacFeelingDownPosi;
    }

    public Integer getCbacFeelingDownScore() {
        return cbacFeelingDownScore;
    }

    public void setCbacFeelingDownScore(Integer cbacFeelingDownScore) {
        this.cbacFeelingDownScore = cbacFeelingDownScore;
    }

    public String getCbacFuelUsed() {
        return cbacFuelUsed;
    }

    public void setCbacFuelUsed(String cbacFuelUsed) {
        this.cbacFuelUsed = cbacFuelUsed;
    }

    public Integer getCbacFuelUsedPosi() {
        return cbacFuelUsedPosi;
    }

    public void setCbacFuelUsedPosi(Integer cbacFuelUsedPosi) {
        this.cbacFuelUsedPosi = cbacFuelUsedPosi;
    }

    public String getCbacGrowthInMouth() {
        return cbacGrowthInMouth;
    }

    public void setCbacGrowthInMouth(String cbacGrowthInMouth) {
        this.cbacGrowthInMouth = cbacGrowthInMouth;
    }

    public Integer getCbacGrowthInMouthPosi() {
        return cbacGrowthInMouthPosi;
    }

    public void setCbacGrowthInMouthPosi(Integer cbacGrowthInMouthPosi) {
        this.cbacGrowthInMouthPosi = cbacGrowthInMouthPosi;
    }

    public String getCbacHyperPigmentedPatch() {
        return cbacHyperPigmentedPatch;
    }

    public void setCbacHyperPigmentedPatch(String cbacHyperPigmentedPatch) {
        this.cbacHyperPigmentedPatch = cbacHyperPigmentedPatch;
    }

    public Integer getCbacHyperPigmentedPatchPosi() {
        return cbacHyperPigmentedPatchPosi;
    }

    public void setCbacHyperPigmentedPatchPosi(Integer cbacHyperPigmentedPatchPosi) {
        this.cbacHyperPigmentedPatchPosi = cbacHyperPigmentedPatchPosi;
    }

    public String getCbacInabilityCloseEyelid() {
        return cbacInabilityCloseEyelid;
    }

    public void setCbacInabilityCloseEyelid(String cbacInabilityCloseEyelid) {
        this.cbacInabilityCloseEyelid = cbacInabilityCloseEyelid;
    }

    public Integer getCbacInabilityCloseEyelidPosi() {
        return cbacInabilityCloseEyelidPosi;
    }

    public void setCbacInabilityCloseEyelidPosi(Integer cbacInabilityCloseEyelidPosi) {
        this.cbacInabilityCloseEyelidPosi = cbacInabilityCloseEyelidPosi;
    }

    public String getCbacLittleInterest() {
        return cbacLittleInterest;
    }

    public void setCbacLittleInterest(String cbacLittleInterest) {
        this.cbacLittleInterest = cbacLittleInterest;
    }

    public Integer getCbacLittleInterestPosi() {
        return cbacLittleInterestPosi;
    }

    public void setCbacLittleInterestPosi(Integer cbacLittleInterestPosi) {
        this.cbacLittleInterestPosi = cbacLittleInterestPosi;
    }

    public Integer getCbacLittleInterestScore() {
        return cbacLittleInterestScore;
    }

    public void setCbacLittleInterestScore(Integer cbacLittleInterestScore) {
        this.cbacLittleInterestScore = cbacLittleInterestScore;
    }

    public String getCbacNodulesOnSkin() {
        return cbacNodulesOnSkin;
    }

    public void setCbacNodulesOnSkin(String cbacNodulesOnSkin) {
        this.cbacNodulesOnSkin = cbacNodulesOnSkin;
    }

    public Integer getCbacNodulesOnSkinPosi() {
        return cbacNodulesOnSkinPosi;
    }

    public void setCbacNodulesOnSkinPosi(Integer cbacNodulesOnSkinPosi) {
        this.cbacNodulesOnSkinPosi = cbacNodulesOnSkinPosi;
    }

    public String getCbacNumbnessOnPalm() {
        return cbacNumbnessOnPalm;
    }

    public void setCbacNumbnessOnPalm(String cbacNumbnessOnPalm) {
        this.cbacNumbnessOnPalm = cbacNumbnessOnPalm;
    }

    public Integer getCbacNumbnessOnPalmPosi() {
        return cbacNumbnessOnPalmPosi;
    }

    public void setCbacNumbnessOnPalmPosi(Integer cbacNumbnessOnPalmPosi) {
        this.cbacNumbnessOnPalmPosi = cbacNumbnessOnPalmPosi;
    }

    public String getCbacOccupationalExposure() {
        return cbacOccupationalExposure;
    }

    public void setCbacOccupationalExposure(String cbacOccupationalExposure) {
        this.cbacOccupationalExposure = cbacOccupationalExposure;
    }

    public Integer getCbacOccupationalExposurePosi() {
        return cbacOccupationalExposurePosi;
    }

    public void setCbacOccupationalExposurePosi(Integer cbacOccupationalExposurePosi) {
        this.cbacOccupationalExposurePosi = cbacOccupationalExposurePosi;
    }

    public String getCbacTinglingOrNumbness() {
        return cbacTinglingOrNumbness;
    }

    public void setCbacTinglingOrNumbness(String cbacTinglingOrNumbness) {
        this.cbacTinglingOrNumbness = cbacTinglingOrNumbness;
    }

    public Integer getCbacTinglingOrNumbnessPosi() {
        return cbacTinglingOrNumbnessPosi;
    }

    public void setCbacTinglingOrNumbnessPosi(Integer cbacTinglingOrNumbnessPosi) {
        this.cbacTinglingOrNumbnessPosi = cbacTinglingOrNumbnessPosi;
    }

    public String getCbacWeeknessInFeet() {
        return cbacWeeknessInFeet;
    }

    public void setCbacWeeknessInFeet(String cbacWeeknessInFeet) {
        this.cbacWeeknessInFeet = cbacWeeknessInFeet;
    }

    public Integer getCbacWeeknessInFeetPosi() {
        return cbacWeeknessInFeetPosi;
    }

    public void setCbacWeeknessInFeetPosi(Integer cbacWeeknessInFeetPosi) {
        this.cbacWeeknessInFeetPosi = cbacWeeknessInFeetPosi;
    }

    public String getSuspectedHrp() {
        return suspectedHrp;
    }

    public void setSuspectedHrp(String suspectedHrp) {
        this.suspectedHrp = suspectedHrp;
    }

    public String getSuspectedNcd() {
        return suspectedNcd;
    }

    public void setSuspectedNcd(String suspectedNcd) {
        this.suspectedNcd = suspectedNcd;
    }

    public String getSuspectedTb() {
        return suspectedTb;
    }

    public void setSuspectedTb(String suspectedTb) {
        this.suspectedTb = suspectedTb;
    }

    public String getSuspectedNcdDiseases() {
        return suspectedNcdDiseases;
    }

    public void setSuspectedNcdDiseases(String suspectedNcdDiseases) {
        this.suspectedNcdDiseases = suspectedNcdDiseases;
    }

    public String getConfirmedNcd() {
        return confirmedNcd;
    }

    public void setConfirmedNcd(String confirmedNcd) {
        this.confirmedNcd = confirmedNcd;
    }

    public String getConfirmedHrp() {
        return confirmedHrp;
    }

    public void setConfirmedHrp(String confirmedHrp) {
        this.confirmedHrp = confirmedHrp;
    }

    public String getConfirmedTb() {
        return confirmedTb;
    }

    public void setConfirmedTb(String confirmedTb) {
        this.confirmedTb = confirmedTb;
    }

    public String getConfirmedNcdDiseases() {
        return confirmedNcdDiseases;
    }

    public void setConfirmedNcdDiseases(String confirmedNcdDiseases) {
        this.confirmedNcdDiseases = confirmedNcdDiseases;
    }

    public String getDiagnosisStatus() {
        return diagnosisStatus;
    }

    public void setDiagnosisStatus(String diagnosisStatus) {
        this.diagnosisStatus = diagnosisStatus;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public String getReservedFor() {
        return reservedFor;
    }

    public void setReservedFor(String reservedFor) {
        this.reservedFor = reservedFor;
    }

    public String getReservedOn() {
        return reservedOn;
    }

    public void setReservedOn(String reservedOn) {
        this.reservedOn = reservedOn;
    }

    public Integer getReservedById() {
        return reservedById;
    }

    public void setReservedById(Integer reservedById) {
        this.reservedById = reservedById;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Timestamp getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Timestamp lastModDate) {
        this.lastModDate = lastModDate;
    }

    public Long getVanSerialNo() {
        return vanSerialNo;
    }

    public void setVanSerialNo(Long vanSerialNo) {
        this.vanSerialNo = vanSerialNo;
    }

    public Integer getVanId() {
        return vanId;
    }

    public void setVanId(Integer vanId) {
        this.vanId = vanId;
    }

    public String getVehicalNo() {
        return vehicalNo;
    }

    public void setVehicalNo(String vehicalNo) {
        this.vehicalNo = vehicalNo;
    }

    public Integer getParkingPlaceId() {
        return parkingPlaceId;
    }

    public void setParkingPlaceId(Integer parkingPlaceId) {
        this.parkingPlaceId = parkingPlaceId;
    }

    public String getSyncedBy() {
        return syncedBy;
    }

    public void setSyncedBy(String syncedBy) {
        this.syncedBy = syncedBy;
    }

    public Timestamp getSyncedDate() {
        return syncedDate;
    }

    public void setSyncedDate(Timestamp syncedDate) {
        this.syncedDate = syncedDate;
    }

    public Integer getProviderServiceMapId() {
        return providerServiceMapId;
    }

    public void setProviderServiceMapId(Integer providerServiceMapId) {
        this.providerServiceMapId = providerServiceMapId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
}
