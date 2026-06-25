package com.iemr.flw.domain.iemr;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import lombok.Data;

import java.util.List;

@Data
public class AbhaApiResponse {

    @JsonProperty("status_code")
    private String statusCode;

    private String message;

    @JsonProperty("data")
    @JsonAlias("object_data")
    private List<AbhaBeneficiaryDTO> data;
}