package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;

public  class PaymentItem {
        @JsonProperty("activity_code")
        public String activityCode;

        @JsonProperty("count")
        public String count;

        @JsonProperty("incentive_amount")
        public String incentiveAmount;

        public PaymentItem(String activityCode, String count, String incentiveAmount) {
            this.activityCode = activityCode;
            this.count = count;
            this.incentiveAmount = incentiveAmount;
        }
    }