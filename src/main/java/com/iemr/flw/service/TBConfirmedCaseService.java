package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.TBConfirmedCaseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TBConfirmedCaseService {

    String save(List<TBConfirmedCaseDTO> tbConfirmedCaseDTO, String token) throws Exception;

    String getByBenId(Long benId, String authorisation) throws Exception;

    String getByUserId(String authorisation) throws Exception;
}
