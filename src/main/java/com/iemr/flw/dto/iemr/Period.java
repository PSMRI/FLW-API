package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public  class Period {
        @JsonProperty("start")
        public String start;

        @JsonProperty("end")
        public String end;

        public Period() {}



    }

