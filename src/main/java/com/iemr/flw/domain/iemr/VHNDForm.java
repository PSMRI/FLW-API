package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "VHND_form",schema = "db_iemr")
@Data
public class VHNDForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "vhnd_date")
    private String vhndDate;

    @Column(name = "place")
    private String place;

    @Column(name = "no_of_beneficiaries_attended")
    private Integer noOfBeneficiariesAttended;

    @Column(name = "image1")
    private String image1;

    @Column(name = "image2")
    private String image2;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "vhnd_place_id")
    private Integer vhndPlaceId;

    @Column(name = "pregnant_women_anc")
    private String pregnantWomenAnc;

    @Column(name = "lactating_mothers_pnc")
    private String lactatingMothersPnc;

    @Column(name = "children_immunization")
    private String childrenImmunization;

    @Column(name = "select_all_education")
    private Boolean selectAllEducation;

    @Column(name = "knowledge_balanced_diet")
    private String knowledgeBalancedDiet;

    @Column(name = "care_during_pregnancy")
    private String careDuringPregnancy;

    @Column(name = "importance_breastfeeding")
    private String importanceBreastfeeding;

    @Column(name = "complementary_feeding")
    private String complementaryFeeding;

    @Column(name = "hygiene_sanitation")
    private String hygieneSanitation;

    @Column(name = "family_planning_healthcare")
    private String familyPlanningHealthcare;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;


}