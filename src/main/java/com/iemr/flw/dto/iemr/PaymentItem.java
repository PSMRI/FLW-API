package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public  class PaymentItem {
        @JsonProperty("activity_code")
        public String activityCode;

        @JsonProperty("count")
        public String count;

        @JsonProperty("incentive_amount")
        public String incentiveAmount;


    }