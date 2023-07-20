package com.iemr.flw.service;

import com.iemr.flw.dto.TBScreeningDTO;
import com.iemr.flw.dto.TBScreeningRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TBScreeningService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save(TBScreeningRequestDTO tbScreeningList) throws Exception;

    List<TBScreeningDTO> getByUserId(Integer userId);
}
