package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.TBConfirmedCaseDTO;
import lombok.Data;
import java.util.List;

@Data
public class TBConfirmedRequestDTO {

    private Long userId;

    private List<TBConfirmedCaseDTO> tbConfirmedList;
}
