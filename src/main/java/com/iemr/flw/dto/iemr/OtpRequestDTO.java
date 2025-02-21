package com.iemr.flw.dto.iemr;

import lombok.Data;

@Data
public class OtpRequestDTO {
    private Long beneficiaryId;
    private String phoneNumber;
    private Integer otp;
}
