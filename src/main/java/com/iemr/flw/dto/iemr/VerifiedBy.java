package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;

public  class VerifiedBy {
        @JsonProperty("employee_id")
        public String employeeId;

        @JsonProperty("name")
        public String name;

        public VerifiedBy() {}

        public VerifiedBy(String employeeId, String name) {
            this.employeeId = employeeId;
            this.name = name;
        }
    }
