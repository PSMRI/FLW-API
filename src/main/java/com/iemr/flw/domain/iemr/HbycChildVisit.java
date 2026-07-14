package com.iemr.flw.domain.iemr;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import org.hibernate.annotations.Type;

@Entity
@Data
@Table(name = "t_hbyc_child_visits",schema = "db_iemr")
public class HbycChildVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "household_id")
    private Long household_id;

    @Column(name = "visit_day")
    private String visit_day; // 3 Months, 6 Months, etc.

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "hbyc_due_date")
    private String hbyc_due_date;

    @Column(name = "hbyc_visit_date")
    private String visit_date;

    @Column(name = "is_baby_alive")
    private Boolean is_baby_alive = true;

    @Column(name = "date_of_death")
    private String date_of_death;

    @Column(name = "reason_for_death")
    private String reason_for_death;

    @Column(name = "place_of_death")
    private String place_of_death;

    @Column(name = "other_place_of_death")
    private String other_place_of_death;

    @Column(name = "baby_weight")
    private Integer baby_weight; // 0.5 - 7.0

    @Column(name = "is_child_sick")
    private Boolean is_child_sick;

    @Column(name = "is_exclusive_breastfeeding")
    private Boolean exclusive_breastfeeding;

    @Column(name = "is_mother_counseled_exbf")
    private Boolean mother_counseled_ebf;

    @Column(name = "has_child_started_complementary_feeding")
    private Boolean has_child_started_complementary_feeding;

    @Column(name = "is_mother_counseled_cf")
    private Boolean mother_counseled_cf;

    @Column(name = "is_weight_recorded_by_aww")
    private Boolean weight_recorded;

    @Column(name = "is_developmental_delay")
    private Boolean developmental_delay;

    @Column(name = "is_measles_vaccine_given")
    private Boolean measles_vaccine;

    @Column(name = "is_vitamin_a_given")
    private Boolean vitamin_a;

    @Column(name = "is_ors_available")
    private Boolean ors_available;

    @Column(name = "is_ifa_syrup_available")
    private Boolean Ifa_available;

    @Column(name = "is_ors_given")
    private Boolean ors_given;

    @Column(name = "is_ifa_syrup_given")
    private Boolean ifa_given;

    @Column(name = "is_handwashing_counseling_given")
    private Boolean handwash_counseling;

    @Column(name = "is_parenting_counseling_given")
    private Boolean parenting_counseling;

    @Column(name = "is_family_planning_counseling_given")
    private Boolean family_planning_counseling;

    @Column(name = "diarrhoea_episode")
    private Boolean diarrhoea_episode;

    @Column(name = "pneumonia_symptoms")
    private Boolean pneumonia_symptoms;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "mcp_card_images", columnDefinition = "json")
    private List<String> mcp_card_images;

    @Column(name = "created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updated_at = LocalDateTime.now();

    @Column(name = "created_by")
    private String created_by;

    @Column(name = "is_breathing_difficulty")
    private Boolean breathing_difficulty;

    @Column(name = "is_complementary_feeding")
    private Boolean complementary_feeding;

    // Getters and Setters
}
