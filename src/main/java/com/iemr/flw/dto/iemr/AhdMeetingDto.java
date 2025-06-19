package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class AhdMeetingDto {
    private Integer userId;
    private List<AhDMeetingFormDTO> entries;
}