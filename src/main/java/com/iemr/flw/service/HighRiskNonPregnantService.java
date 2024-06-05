package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRNonPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HRNonPregnantTrackDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;

public interface HighRiskNonPregnantService {

    String getAllAssessment(GetBenRequestHandler requestDTO);

    String saveAllAssessment(UserDataDTO<HRNonPregnantAssessDTO> requestDTO);

    String getAllTracking(GetBenRequestHandler requestDTO);

    String saveAllTracking(UserDataDTO<HRNonPregnantTrackDTO> requestDTO);
}
