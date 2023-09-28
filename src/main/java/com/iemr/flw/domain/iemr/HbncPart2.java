package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_part2", schema = "db_iemr")
@Data
public class HbncPart2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_no")
    private Integer visitNo;

    private String dateOfVisit;
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

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
