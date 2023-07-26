package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import org.springframework.stereotype.Component;

@Component
public interface TBScreeningService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save(TBScreeningRequestDTO tbScreeningList) throws Exception;

    TBScreeningRequestDTO getByUserId(GetBenRequestHandler userId);
}
