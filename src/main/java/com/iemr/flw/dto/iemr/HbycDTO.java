package com.iemr.flw.dto.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.List;

@Data
public class HbycDTO {

    @SerializedName("visit_day")
    private String visit_day; // 3 Months, 6 Months, etc.

    @SerializedName("due_date")
    private String due_date;

    @SerializedName("visit_date")
    private String visit_date;

    @SerializedName("is_baby_alive")
    private String is_baby_alive; // Yes/No

    @SerializedName("date_of_death")
    private String date_of_death;

    @SerializedName("reason_for_death")
    private String reason_for_death;

    @SerializedName("place_of_death")
    private String place_of_death;

    @SerializedName("other_place_of_death")
    private String other_place_of_death;

    @SerializedName("baby_weight")
    private Integer baby_weight;

    @SerializedName("is_child_sick")
    private String is_child_sick;

    @SerializedName("exclusive_breastfeeding")
    private String exclusive_breastfeeding;

    @SerializedName("mother_counseled_ebf")
    private String mother_counseled_ebf;

    @SerializedName("complementary_feeding")
    private String complementary_feeding;

    @SerializedName("mother_counseled_cf")
    private String mother_counseled_cf;

    @SerializedName("weight_recorded")
    private String weight_recorded;

    @SerializedName("developmental_delay")
    private String developmental_delay;

    @SerializedName("measles_vaccine")
    private String measles_vaccine;

    @SerializedName("vitamin_a")
    private String vitamin_a;

    @SerializedName("ors_available")
    private String ors_available;

    @SerializedName("ifa_available")
    private String ifa_available;

    @SerializedName("ors_given")
    private String ors_given;

    @SerializedName("ifa_given")
    private String ifa_given;

    @SerializedName("handwash_counseling")
    private String handwash_counseling;

    @SerializedName("parenting_counseling")
    private String parenting_counseling;

    @SerializedName("family_planning_counseling")
    private String family_planning_counseling;

    @SerializedName("diarrhoea_episode")
    private String diarrhoea_episode;

    @SerializedName("breathing_difficulty")
    private String breathing_difficulty;

    @SerializedName("temperature_check")
    private BigDecimal temperature_check;

    @SerializedName("mcp_card_images")
    private List<String> mcp_card_images;

    @SerializedName("created_at")
    private LocalDateTime created_at;

    @SerializedName("updated_at")
    private LocalDateTime updated_at;





}
