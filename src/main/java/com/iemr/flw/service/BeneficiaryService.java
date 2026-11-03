package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.dto.iemr.SAMResponseDTO;

import java.util.List;
import java.util.Map;

public interface BeneficiaryService {

    String getBenData(GetBenRequestHandler requestDTO, String authorisation) throws Exception;

    Map<Long, String> getBeneficiaryRecordsByRegIds(List<Long> benRegIds, String authorisation);

    String saveEyeCheckupVsit(List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS,String token);

    List<EyeCheckupRequestDTO> getEyeCheckUpVisit(GetBenRequestHandler request,String token);
}
