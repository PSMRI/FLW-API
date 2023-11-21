package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HbncPart2DTO {

    private Long id;
    private Long benId;
    private Integer visitNo;
    private Timestamp dateOfVisit;
    private String babyTemperature;
    private String babyEyeCondition;
    private Boolean babyUmbilicalBleed;
    private Boolean actionBabyUmbilicalBleed;
    private Float babyWeight;
    private Boolean babyWeightMatchesColor;
    private String babyWeightColorOnScale;
    private Boolean allLimbsLimp;
    private Boolean feedLessStop;
    private Boolean cryWeakStop;
    private Boolean dryBaby;
    private Boolean wrapClothCloseToMother;
    private Boolean exclusiveBreastFeeding;
    private Boolean cordCleanDry;
    private String unusualInBaby;
    private String otherUnusualInBaby;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;
}
