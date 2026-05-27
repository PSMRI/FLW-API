package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class GeneralOpdDto {
    private String beneficiaryName;
    private String age;
    private String gender;
    private String registrationDate;
    private String mobileNumber;
    private String beneficiaryId;
    private String visitDate;
    private String referredTo;
    private String followUpDate;
    private boolean callButtonEnabled;
}