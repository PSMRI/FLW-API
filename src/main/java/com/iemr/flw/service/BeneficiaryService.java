package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.dto.iemr.SAMResponseDTO;

import java.util.List;

public interface BeneficiaryService {

    String getBenData(GetBenRequestHandler requestDTO, String authorisation) throws Exception;

    String saveEyeCheckupVsit(List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS);

    List<EyeCheckupRequestDTO> getEyeCheckUpVisit(GetBenRequestHandler request);
}
