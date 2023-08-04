package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HRPregnantTrackDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;

public interface HighRiskPregnantService {
    String getAllAssessments(GetBenRequestHandler requestDTO);

    String saveAllAssessment(UserDataDTO<HRPregnantAssessDTO> requestDTO);

    String getAllTracking(GetBenRequestHandler requestDTO);

    String saveAllTracking(UserDataDTO<HRPregnantTrackDTO> requestDTO);

}
