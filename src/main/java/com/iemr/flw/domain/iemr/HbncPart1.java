package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_part1", schema = "db_iemr")
@Data
public class HbncPart1 {

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

    @Column(name = "baby_death_date")
    private Timestamp dateOfBabyDeath;

    @Column(name = "baby_death_time")
    private String timeOfBabyDeath;

    @Column(name = "baby_death_place")
    private String placeOfBabyDeath;

    @Column(name = "other_baby_death_place")
    private String otherPlaceOfBabyDeath;

    @Column(name = "is_baby_preterm")
    private Boolean isBabyPreterm;

    @Column(name = "gestational_age")
    private Integer gestationalAge;

    @Column(name = "first_exam_date")
    private Timestamp dateOfFirstExamination;

    @Column(name = "first_exam_time")
    private String timeOfFirstExamination;

    @Column(name = "mother_alive")
    private Boolean motherAlive;

    @Column(name = "mother_death_date")
    private Timestamp dateOfMotherDeath;

    @Column(name = "mother_death_time")
    private String timeOfMotherDeath;

    @Column(name = "mother_death_pace")
    private String placeOfMotherDeath;

    @Column(name = "other_mother_death_place")
    private String otherPlaceOfMotherDeath;

    @Column(name = "has_mother_any_problem")
    private String motherAnyProblem;

    @Column(name = "baby_first_fed")
    private String babyFirstFed;

    @Column(name = "other_baby_first_fed")
    private String otherBabyFirstFed;

    @Column(name = "first_fed_time")
    private String timeBabyFirstFed;

    @Column(name = "how_first_fed")
    private String howBabyTookFirstFeed;

    @Column(name = "has_breast_feed_problem")
    private Boolean motherHasBreastFeedProblem;

    @Column(name = "breast_feed_problem")
    private String motherBreastFeedProblem;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
