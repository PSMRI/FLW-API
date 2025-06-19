package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class EligibleCoupleDTO implements Serializable {

    private Long id;

    private Long benId;

    private Timestamp registrationDate;

    private Long rchId;

    private Integer ageAtMarriage;

    private Long aadhaarNumber;

    private String bankAccountNumber;

    private String bankName;

    private String branchName;

    private String ifsc;

    private Integer numChildren;

    private Integer numLiveChildren;

    private Timestamp dob1;

    private String gender1;

    private Integer marriageFirstChildGap;

    private Timestamp dob2;

    private String gender2;

    private Integer firstAndSecondChildGap;

    private Timestamp dob3;

    private String gender3;

//    private Integer secondAndThirdChildGap;

    private Timestamp dob4;

    private String gender4;

//    private Integer thirdAndFourthChildGap;

    private Timestamp dob5;

    private String gender5;

//    private Integer fourthAndFifthChildGap;

    private Timestamp dob6;

    private String gender6;

//    private Integer fifthAndSixthChildGap;

    private Timestamp dob7;

    private String gender7;

//    private Integer sixthAndSeventhChildGap;

    private Timestamp dob8;

    private String gender8;

//    private Integer seventhAndEighthChildGap;

    private Timestamp dob9;

    private String gender9;

    private String misCarriage;

    private String homeDelivery;

    private String medicalIssues;

    private String pastCSection;

    private Boolean isRegistered;

    private Boolean isHighRisk;

//    private Integer eighthAndNinthChildGap;

    private Timestamp createdDate;

    private String createdBy;

    private Timestamp updatedDate;

    private String updatedBy;


  
    private String lmpDate;

}
