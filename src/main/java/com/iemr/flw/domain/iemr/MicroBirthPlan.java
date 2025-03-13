package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Entity
@Table(name = "t_micro_birth_plan", schema = "db_iemr")
@Data
public class MicroBirthPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "contact_no_1", length = 10)
    private String contactNumber1; // Auto-filled, must be 10 digits, starts with 6-9

    @Column(name = "contact_no_2", length = 10)
    private String contactNumber2; // Optional, 10 digits, starts with 6-9

    @Column(name = "sc_hwc_tg_hosp", length = 100)
    private String scHosp; // Alphanumeric, all caps

    @Column(name = "block", length = 100)
    private String block; // Alphanumeric, all caps


    @Column(name = "nearest_sc_hwc", length = 100)
    private String nearestSc; // Alphanumeric, all caps

    @Column(name = "nearest_phc", length = 100)
    private String nearestPhc; // Alphanumeric, all caps

    @Column(name = "nearest_fru", length = 100)
    private String nearestFru; // Alphanumeric, all caps

    @Column(name = "nearest_usg", length = 100)
    private String usg; // Alphanumeric, all caps

    @Column(name = "blood_group")
    private String bloodGroup; // Spinner: A+, B+, etc.

    @Column(name = "blood_donors", length = 50)
    private String bloodDonors2; // Alphabets only, all caps

    @Column(name = "birth_companion", length = 50)
    private String birthCompanion; // Alphabets only, all caps

    @Column(name = "child_caretaker", length = 50)
    private String careTaker; // Alphabets only, all caps

    @Column(name = "community_support", length = 100)
    private String communityMember; // Alphabets only, all caps

    @Column(name = "community_support_contact", length = 100)
    private String communityMemberContact; // Alphabets only, all caps

    @Column(name = "transportation_mode", length = 100)
    private String modeOfTransportation; // Mode of transport


}