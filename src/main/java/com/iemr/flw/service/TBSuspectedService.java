package com.iemr.flw.service;


import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import org.springframework.stereotype.Component;

@Component
public interface TBSuspectedService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save(TBSuspectedRequestDTO tbScreeningList) throws Exception;

    String getByUserId(GetBenRequestHandler userId);
}
