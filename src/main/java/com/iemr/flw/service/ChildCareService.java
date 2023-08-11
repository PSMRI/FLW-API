package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HbycDTO;

import java.util.List;

public interface ChildCareService {

    String registerHBYC(List<HbycDTO> hbycDTOs);

    List<HbycDTO> getHbycRecords(GetBenRequestHandler dto);
}
