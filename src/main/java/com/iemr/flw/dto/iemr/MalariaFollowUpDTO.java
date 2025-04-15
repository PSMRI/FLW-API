package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class MalariaFollowUpDTO {
    private Integer userId;
    private List<MalariaFollowListUpDTO> malariaFollowListUp;
}
