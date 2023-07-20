package com.iemr.flw.service;

import com.iemr.flw.dto.GetBenRequestHandler;

public interface BeneficiaryService {

    String getBenData(GetBenRequestHandler requestDTO, String authorisation) throws Exception;

}
