package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@Table(name = "asha_profile",schema = "db_iemr")
public class AshaWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "village")
    private String village;

    @Column(name = "employee_id")
    private Integer employeeId;  // Changed from Integer to String

    @Column(name = "dob")
    private LocalDate dob; // Changed from String to LocalDate


    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "alternate_mobile_number")
    private String alternateMobileNumber;

    @Column(name = "father_or_spouse_name")
    private String fatherOrSpouseName;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining; // Changed from String to LocalDate


    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "ifsc")
    private String ifsc;


    @Column(name = "population_covered")
    private Integer populationCovered;


    @Column(name = "cho_name",nullable = false)
    private String choName;


    @Column(name = "cho_mobile")
    private String choMobile;


    @Column(name = "aww_name")
    private String awwName;


    @Column(name = "aww_mobile")
    private String awwMobile;


    @Column(name = "anm1_name")
    private String anm1Name;


    @Column(name = "anm1_mobile")
    private String anm1Mobile;


    @Column(name = "anm2_name")
    private String anm2Name;


    @Column(name = "anm2_mobile")
    private String anm2Mobile;


    @Column(name = "abha_number")
    private String abhaNumber;

    @Column(name = "asha_household_registration")
    private String ashaHouseholdRegistration;

    @Column(name = "asha_family_member")
    private String ashaFamilyMember;

    @Column(name = "ProviderServiceMapID")
    private Integer ProviderServiceMapID;

    @Column(name = "profileImage")
    private String profileImage;

    @Column(name = "isFatherOrSpouse")
    private Boolean isFatherOrSpouse;
    @Column(name = "supervisorName")
    private String  supervisorName;
    @Column(name = "supervisorMobile")
    private String supervisorMobile;

}
