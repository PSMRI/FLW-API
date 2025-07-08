package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "t_hbnc_visit", schema = "db_iemr")
@Data
public class HbncVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "hbnc_visit_day")
    private String hbncVisitDay;

    @Column(name = "hbnc_due_date")
    private LocalDate hbncDueDate;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "baby_alive")
    private Boolean babyAlive = true;

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @Column(name = "reason_for_death")
    private String reasonForDeath;

    @Column(name = "place_of_death")
    private String placeOfDeath;

    @Column(name = "other_place_of_death")
    private String otherPlaceOfDeath;

    @Column(name = "baby_weight")
    private Double babyWeight;

    @Column(name = "urine_passed")
    private Boolean urinePassed;

    @Column(name = "stool_passed")
    private Boolean stoolPassed;

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
    private String chestIndrawing;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "jaundice")
    private Boolean jaundice;

    @Column(name = "umbilical_stump_condition")
    private String umbilicalStumpCondition;

    @Column(name = "baby_discharged_from_sncu")
    private Boolean babyDischargedFromSNCU = false;

    @Column(name = "discharge_summary_image")
    private String dischargeSummaryImage;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate ;

    @Column(name = "updated_date")
    private Timestamp updatedDate ;

    @Column(name = "updated_by")
    private String updatedBy;

}
