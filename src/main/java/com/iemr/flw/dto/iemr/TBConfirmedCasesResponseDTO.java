package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.TBConfirmedCase;
import lombok.Data;

import java.util.List;
@Data
public class TBConfirmedCasesResponseDTO {
    Integer userId ;
    List<TBConfirmedCase> tbConfirmedCases;
}
