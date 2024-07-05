package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
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

    @Column(name = "visit_date")
    private Timestamp dateOfVisit;

    @Column(name = "baby_temp")
    private String babyTemperature;

    @Column(name = "baby_eye_condition")
    private String babyEyeCondition;

    @Column(name = "baby_umbillical_bleed")
    private Boolean babyUmbilicalBleed;

    @Column(name = "action_baby_umbillical_bleed")
    private Boolean actionBabyUmbilicalBleed;

    @Column(name = "baby_weight")
    private Float babyWeight;

    @Column(name = "baby_weight_matched_color")
    private Boolean babyWeightMatchesColor;

    @Column(name = "baby_weight_color_on_scale")
    private String babyWeightColorOnScale;

    @Column(name = "all_limbs_limp")
    private Boolean allLimbsLimp;

    @Column(name = "feed_less_stop")
    private Boolean feedLessStop;

    @Column(name = "cry_weak_stop")
    private Boolean cryWeakStop;

    @Column(name = "dry_baby")
    private Boolean dryBaby;

    @Column(name = "wrap_cloth_close_to_mother")
    private Boolean wrapClothCloseToMother;

    @Column(name = "exclusive_breast_feeding")
    private Boolean exclusiveBreastFeeding;

    @Column(name = "cord_clean_dry")
    private Boolean cordCleanDry;

    @Column(name = "unusual_in_baby")
    private String unusualInBaby;

    @Column(name = "other_unusual_in_baby")
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
