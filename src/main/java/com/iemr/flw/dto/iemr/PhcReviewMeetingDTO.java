package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class PhcReviewMeetingDTO {
    private Integer userId;
    private List<PhcReviewMeetingFormDTO> entries;
}