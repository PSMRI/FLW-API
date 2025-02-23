package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "t_micro_birth_plan", schema = "db_iemr")
@Data
public class MicroBirthPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer UserId;

    @Column(name = "ben_id")
    private  Integer benId;

    @Column(name = "pw_name")
    private String pwName; // Auto-populated

    @Column(name = "age")
    private Integer age; // Auto-populated

    @Column(name = "contact_no_1", length = 10)
    private String contactNo1; // Auto-filled, must be 10 digits, starts with 6-9

    @Column(name = "contact_no_2", length = 10)
    private String contactNo2; // Optional, 10 digits, starts with 6-9

    @Column(name = "sc_hwc_tg_hosp", length = 100)
    private String scHwcTgHosp; // Alphanumeric, all caps

    @Column(name = "block", length = 100)
    private String block; // Alphanumeric, all caps

    @Column(name = "husband_name")
    private String husbandName; // Auto-populated

    @Column(name = "nearest_sc_hwc", length = 100)
    private String nearestScHwc; // Alphanumeric, all caps

    @Column(name = "nearest_phc", length = 100)
    private String nearestPhc; // Alphanumeric, all caps

    @Column(name = "nearest_fru", length = 100)
    private String nearestFru; // Alphanumeric, all caps

    @Column(name = "nearest_usg", length = 100)
    private String nearestUsg; // Alphanumeric, all caps

    @Column(name = "blood_group")
    private String bloodGroup; // Spinner: A+, B+, etc.

    @Column(name = "blood_donors", length = 50)
    private String bloodDonors; // Alphabets only, all caps

    @Column(name = "birth_companion", length = 50)
    private String birthCompanion; // Alphabets only, all caps

    @Column(name = "child_caretaker", length = 50)
    private String childCaretaker; // Alphabets only, all caps

    @Column(name = "community_support", length = 100)
    private String communitySupport; // Alphabets only, all caps

    @Column(name = "transportation_mode", length = 100)
    private String transportationMode; // Mode of transport

    @Column(name = "is_synced")
    private Boolean isSynced;



}