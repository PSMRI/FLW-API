package com.iemr.flw.service;


import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import org.springframework.stereotype.Component;

@Component
public interface TBSuspectedService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save(TBSuspectedRequestDTO tbScreeningList) throws Exception;

    String getByUserId(GetBenRequestHandler userId);

    // One record per row, in the same JSON shape getAll returns (STOP-TB changes feed), keyed by id.
    java.util.Map<Long, String> getRecordsByIds(java.util.Collection<Long> ids);
}
