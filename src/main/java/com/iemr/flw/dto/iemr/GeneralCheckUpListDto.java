package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GeneralCheckUpListDto {
    @JsonProperty("check_up_date")
    private String checkUpDate;

    @JsonProperty("problem_type")
    private String problemType;

    @JsonProperty("problems_g6")
    private String problemsG6;

    @JsonProperty("other_text")
    private String otherTextLegacy;

    private String checkupId;

    private Long ashaId;

    private String checkupDate;

    private String problemTypeId;

    private String problemTypeLabel;

    private List<ProblemDto> problems;

    private String otherText;

    private String priority;

    private Integer masterVersion;

    private String referralReason;

    private String referralDate;

    private String createdBy;

    private Long createdDate;

    private Long updatedDate;


}
