package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.UwinSession;
import com.iemr.flw.dto.iemr.UwinSessionRequestDTO;
import com.iemr.flw.dto.iemr.UwinSessionResponseDTO;

import java.util.List;

public interface UwinSessionService {
    UwinSessionResponseDTO saveSession(UwinSessionRequestDTO req) throws Exception;
    UwinSession updateSession(UwinSessionRequestDTO req, Long recordId,Long activityId) throws Exception;
    List<UwinSessionResponseDTO> getSessionsByAsha(Integer ashaId) throws Exception;
}
