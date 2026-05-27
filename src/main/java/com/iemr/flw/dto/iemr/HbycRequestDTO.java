package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.HBYC;
import lombok.Data;

import java.util.List;

@Data
public class HbycRequestDTO {

    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private String visitDate;
    private String  userName;
    private HbycDTO fields;
}