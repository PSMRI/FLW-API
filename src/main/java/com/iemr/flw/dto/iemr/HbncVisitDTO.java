package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HbncVisitDTO {

    private Long id;
    private Long benId;
    private Integer visitNo;
    private Timestamp dateOfVisit;
    private Boolean babyAlive;
    private Integer numTimesFullMeal24hr;
    private Integer numPadChanged24hr;
    private Boolean babyKeptWarmWinter;
    private Boolean babyFedProperly;
    private Boolean babyCryContinuously;
    private Integer motherTemperature;
    private Boolean foulDischargeFever;
    private Boolean motherSpeakAbnormallyFits;
    private Boolean motherLessNoMilk;
    private Boolean motherBreastProblem;
    private Boolean babyEyesSwollen;
    private Float babyWeight;
    private Integer babyTemperature;
    private Boolean babyYellow;
    private String babyImmunizationStatus;
    private Boolean babyReferred;
    private Timestamp dateOfBabyReferral;
    private String placeOfBabyReferral;
    private String otherPlaceOfBabyReferral;
    private Boolean motherReferred;
    private Timestamp dateOfMotherReferral;
    private String placeOfMotherReferral;
    private String otherPlaceOfMotherReferral;
    private Boolean allLimbsLimp;
    private Boolean feedingLessStopped;
    private Boolean cryWeakStopped;
    private Boolean bloatedStomach;
    private Boolean coldOnTouch;
    private Boolean chestDrawing;
    private Boolean breathFast;
    private String pusNavel;
    private String supervisor;
    private String supervisorName;
    private String supervisorComment;
    private Timestamp supervisorSignDate;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;
}
