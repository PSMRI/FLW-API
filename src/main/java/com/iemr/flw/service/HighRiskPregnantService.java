package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;

public interface HighRiskPregnantService {
    String getAllAssessments(GetBenRequestHandler requestDTO);

    String saveAllAssessment(UserDataDTO requestDTO);

    String saveAllTracking(UserDataDTO requestDTO);

    String getAllTracking(GetBenRequestHandler requestDTO);
}
