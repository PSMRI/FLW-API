package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.TBConfirmedCaseDTO;
import com.iemr.flw.dto.iemr.TbReferralFollowUpDTO;
import com.iemr.flw.dto.iemr.TbTptFollowUpDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TBConfirmedCaseService {

    String save(List<TBConfirmedCaseDTO> tbConfirmedCaseDTO, String token) throws Exception;

    String getByBenId(Long benId, String authorisation) throws Exception;

    String getByUserId(String authorisation) throws Exception;

    String getByProviderServiceMapId(Integer providerServiceMapID, Integer villageID) throws Exception;

    // One record per row, in the same JSON shape getAll returns (STOP-TB changes feed), keyed by id.
    java.util.Map<Long, String> getRecordsByIds(java.util.Collection<Long> ids);

    String saveReferralFollowUp(TbReferralFollowUpDTO requestDTO, String token);

    String getReferralFollowUp(String token);

    String saveTptFollowUp(TbTptFollowUpDTO requestDTO, String token);

    String getTptFollowUp(String token);
}
