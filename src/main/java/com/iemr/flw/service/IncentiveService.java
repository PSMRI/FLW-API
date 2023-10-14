package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IncentiveActivityDTO;
import com.iemr.flw.dto.iemr.IncentiveRequestDTO;

import java.util.List;

public interface IncentiveService {

    String saveIncentivesMaster(List<IncentiveActivityDTO> activityDTOS);

    String getIncentiveMaster(IncentiveRequestDTO incentiveRequestDTO);

    String getAllIncentivesByUserId(GetBenRequestHandler requestDTO);
}
