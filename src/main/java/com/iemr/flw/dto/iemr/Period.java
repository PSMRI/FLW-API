package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;

public  class Period {
        @JsonProperty("start")
        public String start;

        @JsonProperty("end")
        public String end;

        public Period() {}


        public Period(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }

