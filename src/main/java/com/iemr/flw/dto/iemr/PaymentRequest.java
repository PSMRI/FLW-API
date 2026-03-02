package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public  class PaymentRequest {
        @JsonProperty("submission_id")
        public String submissionId;

        @JsonProperty("partner_code")
        public String partnerCode;

        @JsonProperty("period")
        public Period period;

        @JsonProperty("asha_id")
        public String ashaId;

        @JsonProperty("generated_at")
        public String generatedAt;

        @JsonProperty("verified_by")
        public VerifiedBy verifiedBy;

        @JsonProperty("items")
        public List<PaymentItem> items;

        public PaymentRequest() {}

        public PaymentRequest(String submissionId, String partnerCode, Period period,
                              String ashaId, String generatedAt,
                              VerifiedBy verifiedBy, List<PaymentItem> items) {
            this.submissionId = submissionId;
            this.partnerCode = partnerCode;
            this.period = period;
            this.ashaId = ashaId;
            this.generatedAt = generatedAt;
            this.verifiedBy = verifiedBy;
            this.items = items;
        }
    }
