package com.iemr.flw.domain.identity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "i_cbacdetails", schema = "db_identity")
//@Data
//@IdClass(CbacId.class)
public class CbacDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cbacDetailsId;
    private Long beneficiaryRegId;
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

    @Id
    @Column(name = "CBACDetailsId")
    public long getCbacDetailsId() {
        return cbacDetailsId;
    }

    public void setCbacDetailsId(long cbacDetailsId) {
        this.cbacDetailsId = cbacDetailsId;
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
    @Column(name = "cbac_age")
    public String getCbacAge() {
        return cbacAge;
    }

    public void setCbacAge(String cbacAge) {
        this.cbacAge = cbacAge;
    }

    @Basic
    @Column(name = "cbac_age_posi")
    public Integer getCbacAgePosi() {
        return cbacAgePosi;
    }

    public void setCbacAgePosi(Integer cbacAgePosi) {
        this.cbacAgePosi = cbacAgePosi;
    }

    @Basic
    @Column(name = "cbac_alcohol")
    public String getCbacAlcohol() {
        return cbacAlcohol;
    }

    public void setCbacAlcohol(String cbacAlcohol) {
        this.cbacAlcohol = cbacAlcohol;
    }

    @Basic
    @Column(name = "cbac_alcohol_posi")
    public Integer getCbacAlcoholPosi() {
        return cbacAlcoholPosi;
    }

    public void setCbacAlcoholPosi(Integer cbacAlcoholPosi) {
        this.cbacAlcoholPosi = cbacAlcoholPosi;
    }

    @Basic
    @Column(name = "cbac_antitbdrugs")
    public String getCbacAntitbdrugs() {
        return cbacAntitbdrugs;
    }

    public void setCbacAntitbdrugs(String cbacAntitbdrugs) {
        this.cbacAntitbdrugs = cbacAntitbdrugs;
    }

    @Basic
    @Column(name = "cbac_antitbdrugs_pos")
    public Integer getCbacAntitbdrugsPos() {
        return cbacAntitbdrugsPos;
    }

    public void setCbacAntitbdrugsPos(Integer cbacAntitbdrugsPos) {
        this.cbacAntitbdrugsPos = cbacAntitbdrugsPos;
    }

    @Basic
    @Column(name = "cbac_bleedingafterintercourse")
    public String getCbacBleedingafterintercourse() {
        return cbacBleedingafterintercourse;
    }

    public void setCbacBleedingafterintercourse(String cbacBleedingafterintercourse) {
        this.cbacBleedingafterintercourse = cbacBleedingafterintercourse;
    }

    @Basic
    @Column(name = "cbac_bleedingafterintercourse_pos")
    public Integer getCbacBleedingafterintercoursePos() {
        return cbacBleedingafterintercoursePos;
    }

    public void setCbacBleedingafterintercoursePos(Integer cbacBleedingafterintercoursePos) {
        this.cbacBleedingafterintercoursePos = cbacBleedingafterintercoursePos;
    }

    @Basic
    @Column(name = "cbac_bleedingaftermenopause")
    public String getCbacBleedingaftermenopause() {
        return cbacBleedingaftermenopause;
    }

    public void setCbacBleedingaftermenopause(String cbacBleedingaftermenopause) {
        this.cbacBleedingaftermenopause = cbacBleedingaftermenopause;
    }

    @Basic
    @Column(name = "cbac_bleedingaftermenopause_pos")
    public Integer getCbacBleedingaftermenopausePos() {
        return cbacBleedingaftermenopausePos;
    }

    public void setCbacBleedingaftermenopausePos(Integer cbacBleedingaftermenopausePos) {
        this.cbacBleedingaftermenopausePos = cbacBleedingaftermenopausePos;
    }

    @Basic
    @Column(name = "cbac_bleedingbtwnperiods")
    public String getCbacBleedingbtwnperiods() {
        return cbacBleedingbtwnperiods;
    }

    public void setCbacBleedingbtwnperiods(String cbacBleedingbtwnperiods) {
        this.cbacBleedingbtwnperiods = cbacBleedingbtwnperiods;
    }

    @Basic
    @Column(name = "cbac_bleedingbtwnperiods_pos")
    public Integer getCbacBleedingbtwnperiodsPos() {
        return cbacBleedingbtwnperiodsPos;
    }

    public void setCbacBleedingbtwnperiodsPos(Integer cbacBleedingbtwnperiodsPos) {
        this.cbacBleedingbtwnperiodsPos = cbacBleedingbtwnperiodsPos;
    }

    @Basic
    @Column(name = "cbac_blooddischage")
    public String getCbacBlooddischage() {
        return cbacBlooddischage;
    }

    public void setCbacBlooddischage(String cbacBlooddischage) {
        this.cbacBlooddischage = cbacBlooddischage;
    }

    @Basic
    @Column(name = "cbac_blooddischage_pos")
    public Integer getCbacBlooddischagePos() {
        return cbacBlooddischagePos;
    }

    public void setCbacBlooddischagePos(Integer cbacBlooddischagePos) {
        this.cbacBlooddischagePos = cbacBlooddischagePos;
    }

    @Basic
    @Column(name = "cbac_bloodsputum")
    public String getCbacBloodsputum() {
        return cbacBloodsputum;
    }

    public void setCbacBloodsputum(String cbacBloodsputum) {
        this.cbacBloodsputum = cbacBloodsputum;
    }

    @Basic
    @Column(name = "cbac_bloodsputum_pos")
    public Integer getCbacBloodsputumPos() {
        return cbacBloodsputumPos;
    }

    public void setCbacBloodsputumPos(Integer cbacBloodsputumPos) {
        this.cbacBloodsputumPos = cbacBloodsputumPos;
    }

    @Basic
    @Column(name = "cbac_changeinbreast")
    public String getCbacChangeinbreast() {
        return cbacChangeinbreast;
    }

    public void setCbacChangeinbreast(String cbacChangeinbreast) {
        this.cbacChangeinbreast = cbacChangeinbreast;
    }

    @Basic
    @Column(name = "cbac_changeinbreast_pos")
    public Integer getCbacChangeinbreastPos() {
        return cbacChangeinbreastPos;
    }

    public void setCbacChangeinbreastPos(Integer cbacChangeinbreastPos) {
        this.cbacChangeinbreastPos = cbacChangeinbreastPos;
    }

    @Basic
    @Column(name = "cbac_coughing")
    public String getCbacCoughing() {
        return cbacCoughing;
    }

    public void setCbacCoughing(String cbacCoughing) {
        this.cbacCoughing = cbacCoughing;
    }

    @Basic
    @Column(name = "cbac_coughing_pos")
    public Integer getCbacCoughingPos() {
        return cbacCoughingPos;
    }

    public void setCbacCoughingPos(Integer cbacCoughingPos) {
        this.cbacCoughingPos = cbacCoughingPos;
    }

    @Basic
    @Column(name = "cbac_difficultyinmouth")
    public String getCbacDifficultyinmouth() {
        return cbacDifficultyinmouth;
    }

    public void setCbacDifficultyinmouth(String cbacDifficultyinmouth) {
        this.cbacDifficultyinmouth = cbacDifficultyinmouth;
    }

    @Basic
    @Column(name = "cbac_difficultyinmouth_pos")
    public Integer getCbacDifficultyinmouthPos() {
        return cbacDifficultyinmouthPos;
    }

    public void setCbacDifficultyinmouthPos(Integer cbacDifficultyinmouthPos) {
        this.cbacDifficultyinmouthPos = cbacDifficultyinmouthPos;
    }

    @Basic
    @Column(name = "cbac_familyhistory")
    public String getCbacFamilyhistory() {
        return cbacFamilyhistory;
    }

    public void setCbacFamilyhistory(String cbacFamilyhistory) {
        this.cbacFamilyhistory = cbacFamilyhistory;
    }

    @Basic
    @Column(name = "cbac_familyhistory_posi")
    public Integer getCbacFamilyhistoryPosi() {
        return cbacFamilyhistoryPosi;
    }

    public void setCbacFamilyhistoryPosi(Integer cbacFamilyhistoryPosi) {
        this.cbacFamilyhistoryPosi = cbacFamilyhistoryPosi;
    }

    @Basic
    @Column(name = "cbac_fivermore")
    public String getCbacFivermore() {
        return cbacFivermore;
    }

    public void setCbacFivermore(String cbacFivermore) {
        this.cbacFivermore = cbacFivermore;
    }

    @Basic
    @Column(name = "cbac_fivermore_pos")
    public Integer getCbacFivermorePos() {
        return cbacFivermorePos;
    }

    public void setCbacFivermorePos(Integer cbacFivermorePos) {
        this.cbacFivermorePos = cbacFivermorePos;
    }

    @Basic
    @Column(name = "cbac_foulveginaldischarge")
    public String getCbacFoulveginaldischarge() {
        return cbacFoulveginaldischarge;
    }

    public void setCbacFoulveginaldischarge(String cbacFoulveginaldischarge) {
        this.cbacFoulveginaldischarge = cbacFoulveginaldischarge;
    }

    @Basic
    @Column(name = "cbac_foulveginaldischarge_pos")
    public Integer getCbacFoulveginaldischargePos() {
        return cbacFoulveginaldischargePos;
    }

    public void setCbacFoulveginaldischargePos(Integer cbacFoulveginaldischargePos) {
        this.cbacFoulveginaldischargePos = cbacFoulveginaldischargePos;
    }

    @Basic
    @Column(name = "cbac_historyoffits")
    public String getCbacHistoryoffits() {
        return cbacHistoryoffits;
    }

    public void setCbacHistoryoffits(String cbacHistoryoffits) {
        this.cbacHistoryoffits = cbacHistoryoffits;
    }

    @Basic
    @Column(name = "cbac_historyoffits_pos")
    public Integer getCbacHistoryoffitsPos() {
        return cbacHistoryoffitsPos;
    }

    public void setCbacHistoryoffitsPos(Integer cbacHistoryoffitsPos) {
        this.cbacHistoryoffitsPos = cbacHistoryoffitsPos;
    }

    @Basic
    @Column(name = "cbac_loseofweight")
    public String getCbacLoseofweight() {
        return cbacLoseofweight;
    }

    public void setCbacLoseofweight(String cbacLoseofweight) {
        this.cbacLoseofweight = cbacLoseofweight;
    }

    @Basic
    @Column(name = "cbac_loseofweight_pos")
    public Integer getCbacLoseofweightPos() {
        return cbacLoseofweightPos;
    }

    public void setCbacLoseofweightPos(Integer cbacLoseofweightPos) {
        this.cbacLoseofweightPos = cbacLoseofweightPos;
    }

    @Basic
    @Column(name = "cbac_lumpinbreast")
    public String getCbacLumpinbreast() {
        return cbacLumpinbreast;
    }

    public void setCbacLumpinbreast(String cbacLumpinbreast) {
        this.cbacLumpinbreast = cbacLumpinbreast;
    }

    @Basic
    @Column(name = "cbac_lumpinbreast_pos")
    public Integer getCbacLumpinbreastPos() {
        return cbacLumpinbreastPos;
    }

    public void setCbacLumpinbreastPos(Integer cbacLumpinbreastPos) {
        this.cbacLumpinbreastPos = cbacLumpinbreastPos;
    }

    @Basic
    @Column(name = "cbac_nightsweats")
    public String getCbacNightsweats() {
        return cbacNightsweats;
    }

    public void setCbacNightsweats(String cbacNightsweats) {
        this.cbacNightsweats = cbacNightsweats;
    }

    @Basic
    @Column(name = "cbac_nightsweats_pos")
    public Integer getCbacNightsweatsPos() {
        return cbacNightsweatsPos;
    }

    public void setCbacNightsweatsPos(Integer cbacNightsweatsPos) {
        this.cbacNightsweatsPos = cbacNightsweatsPos;
    }

    @Basic
    @Column(name = "cbac_pa")
    public String getCbacPa() {
        return cbacPa;
    }

    public void setCbacPa(String cbacPa) {
        this.cbacPa = cbacPa;
    }

    @Basic
    @Column(name = "cbac_pa_posi")
    public Integer getCbacPaPosi() {
        return cbacPaPosi;
    }

    public void setCbacPaPosi(Integer cbacPaPosi) {
        this.cbacPaPosi = cbacPaPosi;
    }

    @Basic
    @Column(name = "cbac_referpatient_mo")
    public Integer getCbacReferpatientMo() {
        return cbacReferpatientMo;
    }

    public void setCbacReferpatientMo(Integer cbacReferpatientMo) {
        this.cbacReferpatientMo = cbacReferpatientMo;
    }

    @Basic
    @Column(name = "cbac_smoke")
    public String getCbacSmoke() {
        return cbacSmoke;
    }

    public void setCbacSmoke(String cbacSmoke) {
        this.cbacSmoke = cbacSmoke;
    }

    @Basic
    @Column(name = "cbac_smoke_posi")
    public Integer getCbacSmokePosi() {
        return cbacSmokePosi;
    }

    public void setCbacSmokePosi(Integer cbacSmokePosi) {
        this.cbacSmokePosi = cbacSmokePosi;
    }

    @Basic
    @Column(name = "cbac_sortnesofbirth")
    public String getCbacSortnesofbirth() {
        return cbacSortnesofbirth;
    }

    public void setCbacSortnesofbirth(String cbacSortnesofbirth) {
        this.cbacSortnesofbirth = cbacSortnesofbirth;
    }

    @Basic
    @Column(name = "cbac_sortnesofbirth_pos")
    public Integer getCbacSortnesofbirthPos() {
        return cbacSortnesofbirthPos;
    }

    public void setCbacSortnesofbirthPos(Integer cbacSortnesofbirthPos) {
        this.cbacSortnesofbirthPos = cbacSortnesofbirthPos;
    }

    @Basic
    @Column(name = "cbac_sputemcollection")
    public Integer getCbacSputemcollection() {
        return cbacSputemcollection;
    }

    public void setCbacSputemcollection(Integer cbacSputemcollection) {
        this.cbacSputemcollection = cbacSputemcollection;
    }

    @Basic
    @Column(name = "cbac_sufferingtb")
    public String getCbacSufferingtb() {
        return cbacSufferingtb;
    }

    public void setCbacSufferingtb(String cbacSufferingtb) {
        this.cbacSufferingtb = cbacSufferingtb;
    }

    @Basic
    @Column(name = "cbac_sufferingtb_pos")
    public Integer getCbacSufferingtbPos() {
        return cbacSufferingtbPos;
    }

    public void setCbacSufferingtbPos(Integer cbacSufferingtbPos) {
        this.cbacSufferingtbPos = cbacSufferingtbPos;
    }

    @Basic
    @Column(name = "cbac_tbhistory")
    public String getCbacTbhistory() {
        return cbacTbhistory;
    }

    public void setCbacTbhistory(String cbacTbhistory) {
        this.cbacTbhistory = cbacTbhistory;
    }

    @Basic
    @Column(name = "cbac_tbhistory_pos")
    public Integer getCbacTbhistoryPos() {
        return cbacTbhistoryPos;
    }

    public void setCbacTbhistoryPos(Integer cbacTbhistoryPos) {
        this.cbacTbhistoryPos = cbacTbhistoryPos;
    }

    @Basic
    @Column(name = "cbac_toneofvoice")
    public String getCbacToneofvoice() {
        return cbacToneofvoice;
    }

    public void setCbacToneofvoice(String cbacToneofvoice) {
        this.cbacToneofvoice = cbacToneofvoice;
    }

    @Basic
    @Column(name = "cbac_toneofvoice_pos")
    public Integer getCbacToneofvoicePos() {
        return cbacToneofvoicePos;
    }

    public void setCbacToneofvoicePos(Integer cbacToneofvoicePos) {
        this.cbacToneofvoicePos = cbacToneofvoicePos;
    }

    @Basic
    @Column(name = "cbac_tracing_all_fm")
    public String getCbacTracingAllFm() {
        return cbacTracingAllFm;
    }

    public void setCbacTracingAllFm(String cbacTracingAllFm) {
        this.cbacTracingAllFm = cbacTracingAllFm;
    }

    @Basic
    @Column(name = "cbac_uicers")
    public String getCbacUicers() {
        return cbacUicers;
    }

    public void setCbacUicers(String cbacUicers) {
        this.cbacUicers = cbacUicers;
    }

    @Basic
    @Column(name = "cbac_uicers_pos")
    public Integer getCbacUicersPos() {
        return cbacUicersPos;
    }

    public void setCbacUicersPos(Integer cbacUicersPos) {
        this.cbacUicersPos = cbacUicersPos;
    }

    @Basic
    @Column(name = "cbac_waist")
    public String getCbacWaist() {
        return cbacWaist;
    }

    public void setCbacWaist(String cbacWaist) {
        this.cbacWaist = cbacWaist;
    }

    @Basic
    @Column(name = "cbac_waist_posi")
    public Integer getCbacWaistPosi() {
        return cbacWaistPosi;
    }

    public void setCbacWaistPosi(Integer cbacWaistPosi) {
        this.cbacWaistPosi = cbacWaistPosi;
    }

    @Basic
    @Column(name = "houseoldId")
    public Long getHouseoldId() {
        return houseoldId;
    }

    public void setHouseoldId(Long houseoldId) {
        this.houseoldId = houseoldId;
    }

    @Basic
    @Column(name = "countryid")
    public Integer getCountryid() {
        return countryid;
    }

    public void setCountryid(Integer countryid) {
        this.countryid = countryid;
    }

    @Basic
    @Column(name = "stateid")
    public Integer getStateid() {
        return stateid;
    }

    public void setStateid(Integer stateid) {
        this.stateid = stateid;
    }

    @Basic
    @Column(name = "districtid")
    public Integer getDistrictid() {
        return districtid;
    }

    public void setDistrictid(Integer districtid) {
        this.districtid = districtid;
    }

    @Basic
    @Column(name = "districtname")
    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }

    @Basic
    @Column(name = "villageid")
    public Integer getVillageid() {
        return villageid;
    }

    public void setVillageid(Integer villageid) {
        this.villageid = villageid;
    }

    @Basic
    @Column(name = "serverUpdatedStatus")
    public Integer getServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(Integer serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }

    @Basic
    @Column(name = "total_score")
    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    @Basic
    @Column(name = "cbac_Pain_while_chewing")
    public String getCbacPainWhileChewing() {
        return cbacPainWhileChewing;
    }

    public void setCbacPainWhileChewing(String cbacPainWhileChewing) {
        this.cbacPainWhileChewing = cbacPainWhileChewing;
    }

    @Basic
    @Column(name = "cbac_Pain_while_chewing_posi")
    public Integer getCbacPainWhileChewingPosi() {
        return cbacPainWhileChewingPosi;
    }

    public void setCbacPainWhileChewingPosi(Integer cbacPainWhileChewingPosi) {
        this.cbacPainWhileChewingPosi = cbacPainWhileChewingPosi;
    }

    @Basic
    @Column(name = "cbac_any_thickend_skin")
    public String getCbacAnyThickendSkin() {
        return cbacAnyThickendSkin;
    }

    public void setCbacAnyThickendSkin(String cbacAnyThickendSkin) {
        this.cbacAnyThickendSkin = cbacAnyThickendSkin;
    }

    @Basic
    @Column(name = "cbac_any_thickend_skin_posi")
    public Integer getCbacAnyThickendSkinPosi() {
        return cbacAnyThickendSkinPosi;
    }

    public void setCbacAnyThickendSkinPosi(Integer cbacAnyThickendSkinPosi) {
        this.cbacAnyThickendSkinPosi = cbacAnyThickendSkinPosi;
    }

    @Basic
    @Column(name = "cbac_clawing_of_fingers")
    public String getCbacClawingOfFingers() {
        return cbacClawingOfFingers;
    }

    public void setCbacClawingOfFingers(String cbacClawingOfFingers) {
        this.cbacClawingOfFingers = cbacClawingOfFingers;
    }

    @Basic
    @Column(name = "cbac_clawing_of_fingers_posi")
    public Integer getCbacClawingOfFingersPosi() {
        return cbacClawingOfFingersPosi;
    }

    public void setCbacClawingOfFingersPosi(Integer cbacClawingOfFingersPosi) {
        this.cbacClawingOfFingersPosi = cbacClawingOfFingersPosi;
    }

    @Basic
    @Column(name = "cbac_diff_holding_obj")
    public String getCbacDiffHoldingObj() {
        return cbacDiffHoldingObj;
    }

    public void setCbacDiffHoldingObj(String cbacDiffHoldingObj) {
        this.cbacDiffHoldingObj = cbacDiffHoldingObj;
    }

    @Basic
    @Column(name = "cbac_diff_holding_obj_posi")
    public Integer getCbacDiffHoldingObjPosi() {
        return cbacDiffHoldingObjPosi;
    }

    public void setCbacDiffHoldingObjPosi(Integer cbacDiffHoldingObjPosi) {
        this.cbacDiffHoldingObjPosi = cbacDiffHoldingObjPosi;
    }

    @Basic
    @Column(name = "cbac_feeling_down")
    public String getCbacFeelingDown() {
        return cbacFeelingDown;
    }

    public void setCbacFeelingDown(String cbacFeelingDown) {
        this.cbacFeelingDown = cbacFeelingDown;
    }

    @Basic
    @Column(name = "cbac_feeling_down_posi")
    public Integer getCbacFeelingDownPosi() {
        return cbacFeelingDownPosi;
    }

    public void setCbacFeelingDownPosi(Integer cbacFeelingDownPosi) {
        this.cbacFeelingDownPosi = cbacFeelingDownPosi;
    }

    @Basic
    @Column(name = "cbac_feeling_down_score")
    public Integer getCbacFeelingDownScore() {
        return cbacFeelingDownScore;
    }

    public void setCbacFeelingDownScore(Integer cbacFeelingDownScore) {
        this.cbacFeelingDownScore = cbacFeelingDownScore;
    }

    @Basic
    @Column(name = "cbac_fuel_used")
    public String getCbacFuelUsed() {
        return cbacFuelUsed;
    }

    public void setCbacFuelUsed(String cbacFuelUsed) {
        this.cbacFuelUsed = cbacFuelUsed;
    }

    @Basic
    @Column(name = "cbac_fuel_used_posi")
    public Integer getCbacFuelUsedPosi() {
        return cbacFuelUsedPosi;
    }

    public void setCbacFuelUsedPosi(Integer cbacFuelUsedPosi) {
        this.cbacFuelUsedPosi = cbacFuelUsedPosi;
    }

    @Basic
    @Column(name = "cbac_growth_in_mouth")
    public String getCbacGrowthInMouth() {
        return cbacGrowthInMouth;
    }

    public void setCbacGrowthInMouth(String cbacGrowthInMouth) {
        this.cbacGrowthInMouth = cbacGrowthInMouth;
    }

    @Basic
    @Column(name = "cbac_growth_in_mouth_posi")
    public Integer getCbacGrowthInMouthPosi() {
        return cbacGrowthInMouthPosi;
    }

    public void setCbacGrowthInMouthPosi(Integer cbacGrowthInMouthPosi) {
        this.cbacGrowthInMouthPosi = cbacGrowthInMouthPosi;
    }

    @Basic
    @Column(name = "cbac_hyper_pigmented_patch")
    public String getCbacHyperPigmentedPatch() {
        return cbacHyperPigmentedPatch;
    }

    public void setCbacHyperPigmentedPatch(String cbacHyperPigmentedPatch) {
        this.cbacHyperPigmentedPatch = cbacHyperPigmentedPatch;
    }

    @Basic
    @Column(name = "cbac_hyper_pigmented_patch_posi")
    public Integer getCbacHyperPigmentedPatchPosi() {
        return cbacHyperPigmentedPatchPosi;
    }

    public void setCbacHyperPigmentedPatchPosi(Integer cbacHyperPigmentedPatchPosi) {
        this.cbacHyperPigmentedPatchPosi = cbacHyperPigmentedPatchPosi;
    }

    @Basic
    @Column(name = "cbac_inability_close_eyelid")
    public String getCbacInabilityCloseEyelid() {
        return cbacInabilityCloseEyelid;
    }

    public void setCbacInabilityCloseEyelid(String cbacInabilityCloseEyelid) {
        this.cbacInabilityCloseEyelid = cbacInabilityCloseEyelid;
    }

    @Basic
    @Column(name = "cbac_inability_close_eyelid_posi")
    public Integer getCbacInabilityCloseEyelidPosi() {
        return cbacInabilityCloseEyelidPosi;
    }

    public void setCbacInabilityCloseEyelidPosi(Integer cbacInabilityCloseEyelidPosi) {
        this.cbacInabilityCloseEyelidPosi = cbacInabilityCloseEyelidPosi;
    }

    @Basic
    @Column(name = "cbac_little_interest")
    public String getCbacLittleInterest() {
        return cbacLittleInterest;
    }

    public void setCbacLittleInterest(String cbacLittleInterest) {
        this.cbacLittleInterest = cbacLittleInterest;
    }

    @Basic
    @Column(name = "cbac_little_interest_posi")
    public Integer getCbacLittleInterestPosi() {
        return cbacLittleInterestPosi;
    }

    public void setCbacLittleInterestPosi(Integer cbacLittleInterestPosi) {
        this.cbacLittleInterestPosi = cbacLittleInterestPosi;
    }

    @Basic
    @Column(name = "cbac_little_interest_score")
    public Integer getCbacLittleInterestScore() {
        return cbacLittleInterestScore;
    }

    public void setCbacLittleInterestScore(Integer cbacLittleInterestScore) {
        this.cbacLittleInterestScore = cbacLittleInterestScore;
    }

    @Basic
    @Column(name = "cbac_nodules_on_skin")
    public String getCbacNodulesOnSkin() {
        return cbacNodulesOnSkin;
    }

    public void setCbacNodulesOnSkin(String cbacNodulesOnSkin) {
        this.cbacNodulesOnSkin = cbacNodulesOnSkin;
    }

    @Basic
    @Column(name = "cbac_nodules_on_skin_posi")
    public Integer getCbacNodulesOnSkinPosi() {
        return cbacNodulesOnSkinPosi;
    }

    public void setCbacNodulesOnSkinPosi(Integer cbacNodulesOnSkinPosi) {
        this.cbacNodulesOnSkinPosi = cbacNodulesOnSkinPosi;
    }

    @Basic
    @Column(name = "cbac_numbness_on_palm")
    public String getCbacNumbnessOnPalm() {
        return cbacNumbnessOnPalm;
    }

    public void setCbacNumbnessOnPalm(String cbacNumbnessOnPalm) {
        this.cbacNumbnessOnPalm = cbacNumbnessOnPalm;
    }

    @Basic
    @Column(name = "cbac_numbness_on_palm_posi")
    public Integer getCbacNumbnessOnPalmPosi() {
        return cbacNumbnessOnPalmPosi;
    }

    public void setCbacNumbnessOnPalmPosi(Integer cbacNumbnessOnPalmPosi) {
        this.cbacNumbnessOnPalmPosi = cbacNumbnessOnPalmPosi;
    }

    @Basic
    @Column(name = "cbac_occupational_exposure")
    public String getCbacOccupationalExposure() {
        return cbacOccupationalExposure;
    }

    public void setCbacOccupationalExposure(String cbacOccupationalExposure) {
        this.cbacOccupationalExposure = cbacOccupationalExposure;
    }

    @Basic
    @Column(name = "cbac_occupational_exposure_posi")
    public Integer getCbacOccupationalExposurePosi() {
        return cbacOccupationalExposurePosi;
    }

    public void setCbacOccupationalExposurePosi(Integer cbacOccupationalExposurePosi) {
        this.cbacOccupationalExposurePosi = cbacOccupationalExposurePosi;
    }

    @Basic
    @Column(name = "cbac_tingling_or_numbness")
    public String getCbacTinglingOrNumbness() {
        return cbacTinglingOrNumbness;
    }

    public void setCbacTinglingOrNumbness(String cbacTinglingOrNumbness) {
        this.cbacTinglingOrNumbness = cbacTinglingOrNumbness;
    }

    @Basic
    @Column(name = "cbac_tingling_or_numbness_posi")
    public Integer getCbacTinglingOrNumbnessPosi() {
        return cbacTinglingOrNumbnessPosi;
    }

    public void setCbacTinglingOrNumbnessPosi(Integer cbacTinglingOrNumbnessPosi) {
        this.cbacTinglingOrNumbnessPosi = cbacTinglingOrNumbnessPosi;
    }

    @Basic
    @Column(name = "cbac_weekness_in_feet")
    public String getCbacWeeknessInFeet() {
        return cbacWeeknessInFeet;
    }

    public void setCbacWeeknessInFeet(String cbacWeeknessInFeet) {
        this.cbacWeeknessInFeet = cbacWeeknessInFeet;
    }

    @Basic
    @Column(name = "cbac_weekness_in_feet_posi")
    public Integer getCbacWeeknessInFeetPosi() {
        return cbacWeeknessInFeetPosi;
    }

    public void setCbacWeeknessInFeetPosi(Integer cbacWeeknessInFeetPosi) {
        this.cbacWeeknessInFeetPosi = cbacWeeknessInFeetPosi;
    }

    @Basic
    @Column(name = "suspected_hrp")
    public String getSuspectedHrp() {
        return suspectedHrp;
    }

    public void setSuspectedHrp(String suspectedHrp) {
        this.suspectedHrp = suspectedHrp;
    }

    @Basic
    @Column(name = "suspected_ncd")
    public String getSuspectedNcd() {
        return suspectedNcd;
    }

    public void setSuspectedNcd(String suspectedNcd) {
        this.suspectedNcd = suspectedNcd;
    }

    @Basic
    @Column(name = "suspected_tb")
    public String getSuspectedTb() {
        return suspectedTb;
    }

    public void setSuspectedTb(String suspectedTb) {
        this.suspectedTb = suspectedTb;
    }

    @Basic
    @Column(name = "suspected_ncd_diseases")
    public String getSuspectedNcdDiseases() {
        return suspectedNcdDiseases;
    }

    public void setSuspectedNcdDiseases(String suspectedNcdDiseases) {
        this.suspectedNcdDiseases = suspectedNcdDiseases;
    }

    @Basic
    @Column(name = "confirmed_ncd")
    public String getConfirmedNcd() {
        return confirmedNcd;
    }

    public void setConfirmedNcd(String confirmedNcd) {
        this.confirmedNcd = confirmedNcd;
    }

    @Basic
    @Column(name = "confirmed_hrp")
    public String getConfirmedHrp() {
        return confirmedHrp;
    }

    public void setConfirmedHrp(String confirmedHrp) {
        this.confirmedHrp = confirmedHrp;
    }

    @Basic
    @Column(name = "confirmed_tb")
    public String getConfirmedTb() {
        return confirmedTb;
    }

    public void setConfirmedTb(String confirmedTb) {
        this.confirmedTb = confirmedTb;
    }

    @Basic
    @Column(name = "confirmed_ncd_diseases")
    public String getConfirmedNcdDiseases() {
        return confirmedNcdDiseases;
    }

    public void setConfirmedNcdDiseases(String confirmedNcdDiseases) {
        this.confirmedNcdDiseases = confirmedNcdDiseases;
    }

    @Basic
    @Column(name = "diagnosis_status")
    public String getDiagnosisStatus() {
        return diagnosisStatus;
    }

    public void setDiagnosisStatus(String diagnosisStatus) {
        this.diagnosisStatus = diagnosisStatus;
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
    @Column(name = "Reserved")
    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    @Basic
    @Column(name = "ReservedFor")
    public String getReservedFor() {
        return reservedFor;
    }

    public void setReservedFor(String reservedFor) {
        this.reservedFor = reservedFor;
    }

    @Basic
    @Column(name = "ReservedOn")
    public String getReservedOn() {
        return reservedOn;
    }

    public void setReservedOn(String reservedOn) {
        this.reservedOn = reservedOn;
    }

    @Basic
    @Column(name = "ReservedById")
    public Integer getReservedById() {
        return reservedById;
    }

    public void setReservedById(Integer reservedById) {
        this.reservedById = reservedById;
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
    @Column(name = "ProviderServiceMapID")
    public Integer getProviderServiceMapId() {
        return providerServiceMapId;
    }

    public void setProviderServiceMapId(Integer providerServiceMapId) {
        this.providerServiceMapId = providerServiceMapId;
    }

    @Basic
    @Column(name = "deviceId")
    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CbacDetails that = (CbacDetails) o;
        return cbacDetailsId == that.cbacDetailsId &&
                Objects.equals(beneficiaryRegId, that.beneficiaryRegId) &&
                Objects.equals(cbacAge, that.cbacAge) &&
                Objects.equals(cbacAgePosi, that.cbacAgePosi) &&
                Objects.equals(cbacAlcohol, that.cbacAlcohol) &&
                Objects.equals(cbacAlcoholPosi, that.cbacAlcoholPosi) &&
                Objects.equals(cbacAntitbdrugs, that.cbacAntitbdrugs) &&
                Objects.equals(cbacAntitbdrugsPos, that.cbacAntitbdrugsPos) &&
                Objects.equals(cbacBleedingafterintercourse, that.cbacBleedingafterintercourse) &&
                Objects.equals(cbacBleedingafterintercoursePos, that.cbacBleedingafterintercoursePos) &&
                Objects.equals(cbacBleedingaftermenopause, that.cbacBleedingaftermenopause) &&
                Objects.equals(cbacBleedingaftermenopausePos, that.cbacBleedingaftermenopausePos) &&
                Objects.equals(cbacBleedingbtwnperiods, that.cbacBleedingbtwnperiods) &&
                Objects.equals(cbacBleedingbtwnperiodsPos, that.cbacBleedingbtwnperiodsPos) &&
                Objects.equals(cbacBlooddischage, that.cbacBlooddischage) &&
                Objects.equals(cbacBlooddischagePos, that.cbacBlooddischagePos) &&
                Objects.equals(cbacBloodsputum, that.cbacBloodsputum) &&
                Objects.equals(cbacBloodsputumPos, that.cbacBloodsputumPos) &&
                Objects.equals(cbacChangeinbreast, that.cbacChangeinbreast) &&
                Objects.equals(cbacChangeinbreastPos, that.cbacChangeinbreastPos) &&
                Objects.equals(cbacCoughing, that.cbacCoughing) &&
                Objects.equals(cbacCoughingPos, that.cbacCoughingPos) &&
                Objects.equals(cbacDifficultyinmouth, that.cbacDifficultyinmouth) &&
                Objects.equals(cbacDifficultyinmouthPos, that.cbacDifficultyinmouthPos) &&
                Objects.equals(cbacFamilyhistory, that.cbacFamilyhistory) &&
                Objects.equals(cbacFamilyhistoryPosi, that.cbacFamilyhistoryPosi) &&
                Objects.equals(cbacFivermore, that.cbacFivermore) &&
                Objects.equals(cbacFivermorePos, that.cbacFivermorePos) &&
                Objects.equals(cbacFoulveginaldischarge, that.cbacFoulveginaldischarge) &&
                Objects.equals(cbacFoulveginaldischargePos, that.cbacFoulveginaldischargePos) &&
                Objects.equals(cbacHistoryoffits, that.cbacHistoryoffits) &&
                Objects.equals(cbacHistoryoffitsPos, that.cbacHistoryoffitsPos) &&
                Objects.equals(cbacLoseofweight, that.cbacLoseofweight) &&
                Objects.equals(cbacLoseofweightPos, that.cbacLoseofweightPos) &&
                Objects.equals(cbacLumpinbreast, that.cbacLumpinbreast) &&
                Objects.equals(cbacLumpinbreastPos, that.cbacLumpinbreastPos) &&
                Objects.equals(cbacNightsweats, that.cbacNightsweats) &&
                Objects.equals(cbacNightsweatsPos, that.cbacNightsweatsPos) &&
                Objects.equals(cbacPa, that.cbacPa) &&
                Objects.equals(cbacPaPosi, that.cbacPaPosi) &&
                Objects.equals(cbacReferpatientMo, that.cbacReferpatientMo) &&
                Objects.equals(cbacSmoke, that.cbacSmoke) &&
                Objects.equals(cbacSmokePosi, that.cbacSmokePosi) &&
                Objects.equals(cbacSortnesofbirth, that.cbacSortnesofbirth) &&
                Objects.equals(cbacSortnesofbirthPos, that.cbacSortnesofbirthPos) &&
                Objects.equals(cbacSputemcollection, that.cbacSputemcollection) &&
                Objects.equals(cbacSufferingtb, that.cbacSufferingtb) &&
                Objects.equals(cbacSufferingtbPos, that.cbacSufferingtbPos) &&
                Objects.equals(cbacTbhistory, that.cbacTbhistory) &&
                Objects.equals(cbacTbhistoryPos, that.cbacTbhistoryPos) &&
                Objects.equals(cbacToneofvoice, that.cbacToneofvoice) &&
                Objects.equals(cbacToneofvoicePos, that.cbacToneofvoicePos) &&
                Objects.equals(cbacTracingAllFm, that.cbacTracingAllFm) &&
                Objects.equals(cbacUicers, that.cbacUicers) &&
                Objects.equals(cbacUicersPos, that.cbacUicersPos) &&
                Objects.equals(cbacWaist, that.cbacWaist) &&
                Objects.equals(cbacWaistPosi, that.cbacWaistPosi) &&
                Objects.equals(houseoldId, that.houseoldId) &&
                Objects.equals(countryid, that.countryid) &&
                Objects.equals(stateid, that.stateid) &&
                Objects.equals(districtid, that.districtid) &&
                Objects.equals(districtname, that.districtname) &&
                Objects.equals(villageid, that.villageid) &&
                Objects.equals(serverUpdatedStatus, that.serverUpdatedStatus) &&
                Objects.equals(totalScore, that.totalScore) &&
                Objects.equals(cbacPainWhileChewing, that.cbacPainWhileChewing) &&
                Objects.equals(cbacPainWhileChewingPosi, that.cbacPainWhileChewingPosi) &&
                Objects.equals(cbacAnyThickendSkin, that.cbacAnyThickendSkin) &&
                Objects.equals(cbacAnyThickendSkinPosi, that.cbacAnyThickendSkinPosi) &&
                Objects.equals(cbacClawingOfFingers, that.cbacClawingOfFingers) &&
                Objects.equals(cbacClawingOfFingersPosi, that.cbacClawingOfFingersPosi) &&
                Objects.equals(cbacDiffHoldingObj, that.cbacDiffHoldingObj) &&
                Objects.equals(cbacDiffHoldingObjPosi, that.cbacDiffHoldingObjPosi) &&
                Objects.equals(cbacFeelingDown, that.cbacFeelingDown) &&
                Objects.equals(cbacFeelingDownPosi, that.cbacFeelingDownPosi) &&
                Objects.equals(cbacFeelingDownScore, that.cbacFeelingDownScore) &&
                Objects.equals(cbacFuelUsed, that.cbacFuelUsed) &&
                Objects.equals(cbacFuelUsedPosi, that.cbacFuelUsedPosi) &&
                Objects.equals(cbacGrowthInMouth, that.cbacGrowthInMouth) &&
                Objects.equals(cbacGrowthInMouthPosi, that.cbacGrowthInMouthPosi) &&
                Objects.equals(cbacHyperPigmentedPatch, that.cbacHyperPigmentedPatch) &&
                Objects.equals(cbacHyperPigmentedPatchPosi, that.cbacHyperPigmentedPatchPosi) &&
                Objects.equals(cbacInabilityCloseEyelid, that.cbacInabilityCloseEyelid) &&
                Objects.equals(cbacInabilityCloseEyelidPosi, that.cbacInabilityCloseEyelidPosi) &&
                Objects.equals(cbacLittleInterest, that.cbacLittleInterest) &&
                Objects.equals(cbacLittleInterestPosi, that.cbacLittleInterestPosi) &&
                Objects.equals(cbacLittleInterestScore, that.cbacLittleInterestScore) &&
                Objects.equals(cbacNodulesOnSkin, that.cbacNodulesOnSkin) &&
                Objects.equals(cbacNodulesOnSkinPosi, that.cbacNodulesOnSkinPosi) &&
                Objects.equals(cbacNumbnessOnPalm, that.cbacNumbnessOnPalm) &&
                Objects.equals(cbacNumbnessOnPalmPosi, that.cbacNumbnessOnPalmPosi) &&
                Objects.equals(cbacOccupationalExposure, that.cbacOccupationalExposure) &&
                Objects.equals(cbacOccupationalExposurePosi, that.cbacOccupationalExposurePosi) &&
                Objects.equals(cbacTinglingOrNumbness, that.cbacTinglingOrNumbness) &&
                Objects.equals(cbacTinglingOrNumbnessPosi, that.cbacTinglingOrNumbnessPosi) &&
                Objects.equals(cbacWeeknessInFeet, that.cbacWeeknessInFeet) &&
                Objects.equals(cbacWeeknessInFeetPosi, that.cbacWeeknessInFeetPosi) &&
                Objects.equals(suspectedHrp, that.suspectedHrp) &&
                Objects.equals(suspectedNcd, that.suspectedNcd) &&
                Objects.equals(suspectedTb, that.suspectedTb) &&
                Objects.equals(suspectedNcdDiseases, that.suspectedNcdDiseases) &&
                Objects.equals(confirmedNcd, that.confirmedNcd) &&
                Objects.equals(confirmedHrp, that.confirmedHrp) &&
                Objects.equals(confirmedTb, that.confirmedTb) &&
                Objects.equals(confirmedNcdDiseases, that.confirmedNcdDiseases) &&
                Objects.equals(diagnosisStatus, that.diagnosisStatus) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(processed, that.processed) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(reserved, that.reserved) &&
                Objects.equals(reservedFor, that.reservedFor) &&
                Objects.equals(reservedOn, that.reservedOn) &&
                Objects.equals(reservedById, that.reservedById) &&
                Objects.equals(modifiedBy, that.modifiedBy) &&
                Objects.equals(lastModDate, that.lastModDate) &&
                Objects.equals(vanSerialNo, that.vanSerialNo) &&
                Objects.equals(vanId, that.vanId) &&
                Objects.equals(vehicalNo, that.vehicalNo) &&
                Objects.equals(parkingPlaceId, that.parkingPlaceId) &&
                Objects.equals(syncedBy, that.syncedBy) &&
                Objects.equals(syncedDate, that.syncedDate) &&
                Objects.equals(providerServiceMapId, that.providerServiceMapId) &&
                Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cbacDetailsId, beneficiaryRegId, cbacAge, cbacAgePosi, cbacAlcohol, cbacAlcoholPosi, cbacAntitbdrugs, cbacAntitbdrugsPos, cbacBleedingafterintercourse, cbacBleedingafterintercoursePos, cbacBleedingaftermenopause, cbacBleedingaftermenopausePos, cbacBleedingbtwnperiods, cbacBleedingbtwnperiodsPos, cbacBlooddischage, cbacBlooddischagePos, cbacBloodsputum, cbacBloodsputumPos, cbacChangeinbreast, cbacChangeinbreastPos, cbacCoughing, cbacCoughingPos, cbacDifficultyinmouth, cbacDifficultyinmouthPos, cbacFamilyhistory, cbacFamilyhistoryPosi, cbacFivermore, cbacFivermorePos, cbacFoulveginaldischarge, cbacFoulveginaldischargePos, cbacHistoryoffits, cbacHistoryoffitsPos, cbacLoseofweight, cbacLoseofweightPos, cbacLumpinbreast, cbacLumpinbreastPos, cbacNightsweats, cbacNightsweatsPos, cbacPa, cbacPaPosi, cbacReferpatientMo, cbacSmoke, cbacSmokePosi, cbacSortnesofbirth, cbacSortnesofbirthPos, cbacSputemcollection, cbacSufferingtb, cbacSufferingtbPos, cbacTbhistory, cbacTbhistoryPos, cbacToneofvoice, cbacToneofvoicePos, cbacTracingAllFm, cbacUicers, cbacUicersPos, cbacWaist, cbacWaistPosi, houseoldId, countryid, stateid, districtid, districtname, villageid, serverUpdatedStatus, totalScore, cbacPainWhileChewing, cbacPainWhileChewingPosi, cbacAnyThickendSkin, cbacAnyThickendSkinPosi, cbacClawingOfFingers, cbacClawingOfFingersPosi, cbacDiffHoldingObj, cbacDiffHoldingObjPosi, cbacFeelingDown, cbacFeelingDownPosi, cbacFeelingDownScore, cbacFuelUsed, cbacFuelUsedPosi, cbacGrowthInMouth, cbacGrowthInMouthPosi, cbacHyperPigmentedPatch, cbacHyperPigmentedPatchPosi, cbacInabilityCloseEyelid, cbacInabilityCloseEyelidPosi, cbacLittleInterest, cbacLittleInterestPosi, cbacLittleInterestScore, cbacNodulesOnSkin, cbacNodulesOnSkinPosi, cbacNumbnessOnPalm, cbacNumbnessOnPalmPosi, cbacOccupationalExposure, cbacOccupationalExposurePosi, cbacTinglingOrNumbness, cbacTinglingOrNumbnessPosi, cbacWeeknessInFeet, cbacWeeknessInFeetPosi, suspectedHrp, suspectedNcd, suspectedTb, suspectedNcdDiseases, confirmedNcd, confirmedHrp, confirmedTb, confirmedNcdDiseases, diagnosisStatus, deleted, processed, createdBy, createdDate, reserved, reservedFor, reservedOn, reservedById, modifiedBy, lastModDate, vanSerialNo, vanId, vehicalNo, parkingPlaceId, syncedBy, syncedDate, providerServiceMapId, deviceId);
    }
}
