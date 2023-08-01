package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;

public interface HighRiskNonPregnantService {


    String getAllAssessment(GetBenRequestHandler requestDTO);

    String saveAllAssessment(TBSuspectedRequestDTO requestDTO);

    String getAllTracking(GetBenRequestHandler requestDTO);

    String saveAllTracking(TBSuspectedRequestDTO requestDTO);
}
