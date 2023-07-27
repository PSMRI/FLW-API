package com.iemr.flw.service;

import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;

import java.util.List;

public interface CbacService {

    String save(List<CbacDTO> cbacList, String user) throws Exception;

    List<CbacDTO> getByUserId(GetBenRequestHandler requestDTO);
}
