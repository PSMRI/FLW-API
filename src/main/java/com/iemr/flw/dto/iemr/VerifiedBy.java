package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public  class VerifiedBy {
        @JsonProperty("employee_id")
        public String employeeId;

        @JsonProperty("name")
        public String name;

        public VerifiedBy() {}


    }
