package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_visit", schema = "db_iemr")
@Data
public class HbncVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_no")
    private Integer visitNo;

    @Column(name = "visit_date")
    private Timestamp dateOfVisit;

    @Column(name = "baby_alive")
    private Boolean babyAlive;

    @Column(name = "num_times_full_meal")
    private Integer numTimesFullMeal24hr;

    @Column(name = "num_pad_changed")
    private Integer numPadChanged24hr;

    @Column(name = "baby_kept_warm")
    private Boolean babyKeptWarmWinter;

    @Column(name = "baby_fed_properly")
    private Boolean babyFedProperly;

    @Column(name = "baby_cry_continuously")
    private Boolean babyCryContinuously;

    @Column(name = "mother_temp")
    private Integer motherTemperature;

    @Column(name = "foul_discharge")
    private Boolean foulDischargeFever;

    @Column(name = "mother_speaks_abnormally")
    private Boolean motherSpeakAbnormallyFits;

    @Column(name = "mother_less_no_milk")
    private Boolean motherLessNoMilk;

    @Column(name = "mother_breast_problem")
    private Boolean motherBreastProblem;

    @Column(name = "baby_eyes_swollen")
    private Boolean babyEyesSwollen;

    @Column(name = "baby_weight")
    private Float babyWeight;

    @Column(name = "baby_temp")
    private Integer babyTemperature;

    @Column(name = "baby_yellow")
    private Boolean babyYellow;

    @Column(name = "baby_immunization_status")
    private String babyImmunizationStatus;

    @Column(name = "baby_referred")
    private Boolean babyReferred;

    @Column(name = "baby_referral_date")
    private Timestamp dateOfBabyReferral;

    @Column(name = "baby_referral_place")
    private String placeOfBabyReferral;

    @Column(name = "baby_referral_place_other")
    private String otherPlaceOfBabyReferral;

    @Column(name = "mother_referred")
    private Boolean motherReferred;

    @Column(name = "mother_referral_date")
    private Timestamp dateOfMotherReferral;

    @Column(name = "mother_referral_place")
    private String placeOfMotherReferral;

    @Column(name = "mother_referral_place_other")
    private String otherPlaceOfMotherReferral;

    @Column(name = "all_limbs_limp")
    private Boolean allLimbsLimp;

    @Column(name = "feeding_less_stopped")
    private Boolean feedingLessStopped;

    @Column(name = "crt_weak_stopped")
    private Boolean cryWeakStopped;

    @Column(name = "bloated_stomach")
    private Boolean bloatedStomach;

    @Column(name = "cold_on_touch")
    private Boolean coldOnTouch;

    @Column(name = "chest_drawing")
    private Boolean chestDrawing;

    @Column(name = "breath_fast")
    private Boolean breathFast;

    @Column(name = "pus_navel")
    private String pusNavel;

    @Column(name = "supervisor")
    private String supervisor;

    @Column(name = "supervisor_name")
    private String supervisorName;

    @Column(name = "supervisor_comment")
    private String supervisorComment;

    @Column(name = "supervisor_sign_date")
    private Timestamp supervisorSignDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
