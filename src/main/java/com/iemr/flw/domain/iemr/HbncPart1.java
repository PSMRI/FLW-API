package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_part2", schema = "db_iemr")
@Data
public class HbncPart1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_no")
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

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
