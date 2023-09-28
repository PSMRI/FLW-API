package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HbncPart1DTO {

    private Long id;
    private Long benId;
    private Integer visitNo;
    private String dateOfVisit;
    private Boolean babyAlive;
    private Timestamp dateOfBabyDeath;
    private String timeOfBabyDeath;
    private String placeOfBabyDeath;
    private String otherPlaceOfBabyDeath;
    private Boolean isBabyPreterm;
    private String gestationalAge;
    private Timestamp dateOfFirstExamination;
    private String timeOfFirstExamination;
    private Boolean motherAlive;
    private Timestamp dateOfMotherDeath;
    private String timeOfMotherDeath;
    private String placeOfMotherDeath;
    private String otherPlaceOfMotherDeath;
    private String motherAnyProblem;
    private String babyFirstFed;
    private String otherBabyFirstFed;
    private String timeBabyFirstFed;
    private String howBabyTookFirstFeed;
    private Boolean motherHasBreastFeedProblem;
    private String motherBreastFeedProblem;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String updatedBy;
}
