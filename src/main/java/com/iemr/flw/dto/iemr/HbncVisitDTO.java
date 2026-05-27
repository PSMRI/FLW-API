package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

 @Data
 public class HbncVisitDTO {
 @SerializedName("id")
 private Integer id;

  @SerializedName("visit_day")
  private String visit_day;

  @SerializedName("visit_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") // match your actual input
  private String visit_date;

  @SerializedName("is_baby_alive")
  private String is_baby_alive;


  @SerializedName("baby_weight")
  private Double baby_weight;

  @SerializedName("urine_passed")
  private String urine_passed;

  @SerializedName("stool_passed")
  private String stool_passed;

  @SerializedName("diarrhoea")
  private String diarrhoea;

  @SerializedName("vomiting")
  private String vomiting;

  @SerializedName("convulsions")
  private String convulsions;

  @SerializedName("jaundice")
  private String jaundice;

  @SerializedName("discharged_from_sncu")
  private String discharged_from_sncu;

  @SerializedName("activity")
  private String activity;

  @SerializedName("sucking")
  private String sucking;

  @SerializedName("breathing")
  private String breathing;

  @SerializedName("chest_indrawing")
  private String chest_indrawing;

  @SerializedName("temperature")
  private String temperature;

  @SerializedName("umbilical_stump")
  private String umbilical_stump;

  @SerializedName("discharge_summary_upload")
  private String discharge_summary_upload;

  @SerializedName("due_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private String due_date;

  @Column(name = "date_of_death")
  private String date_of_death;

  @Column(name = "reason_for_death")
  private String reasonForDeath;

  @Column(name = "place_of_death")
  private String place_of_death;

  @Column(name = "other_place_of_death")
  private String other_place_of_death;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "baby_eyes_swollen")
  private Boolean babyEyesSwollen;


 }



 
