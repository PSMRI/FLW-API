package com.iemr.flw.domain.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_visit", schema = "db_iemr")
@Data
public class HbncVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "asha_id")
    private Integer ashaId;

    @Column(name = "ben_id")
    private Long beneficiaryId;

    @Column(name = "household_id")
    private Long houseHoldId;

    @Column(name = "hbnc_visit_day")
    private String visit_day;

    @Column(name = "hbnc_due_date")
    private String due_date;

    @Column(name = "visit_date")
    private String visit_date;

    @Column(name = "baby_alive")
    private Boolean is_baby_alive ;

    @Column(name = "date_of_death")
    private String date_of_death;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @Column(name = "place_of_death")
    private String place_of_death;

    @Column(name = "other_place_of_death")
    private String other_place_of_death;

    @Column(name = "baby_weight")
    private Double baby_weight;

    @Column(name = "urine_passed")
    private Boolean urine_passed;

    @Column(name = "stool_passed")
    private Boolean stool_passed;

    @Column(name = "diarrhoea")
    private Boolean diarrhoea;

    @Column(name = "vomiting")
    private Boolean vomiting;

    @Column(name = "convulsions")
    private Boolean convulsions;

    @Column(name = "activity")
    private String activity;

    @Column(name = "sucking")
    private String sucking;

    @Column(name = "breathing")
    private String breathing;

    @Column(name = "chest_indrawing")
    private String chest_indrawing;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "jaundice")
    private Boolean jaundice;

    @Column(name = "umbilical_stump_condition")
    private String umbilical_stump;

    @Column(name = "baby_discharged_from_sncu")
    private Boolean discharged_from_sncu;

    @Column(name = "discharge_summary_image")
    private String discharge_summary_upload;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "baby_eyes_swollen")
    private Boolean babyEyesSwollen;

};
