package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ChildRegisterDTO;

import java.util.List;

public interface ChildService {

    String save(List<ChildRegisterDTO> childRegisterDTOList) throws Exception;

    String getByUserId(GetBenRequestHandler requestDTO);
}
