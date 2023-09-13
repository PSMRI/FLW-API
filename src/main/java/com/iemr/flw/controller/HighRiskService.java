package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HighRiskAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;

public interface HighRiskService {

    String getAllAssessments(GetBenRequestHandler requestDTO);

    String saveAllAssessment(UserDataDTO<HighRiskAssessDTO> requestDTO);
}
