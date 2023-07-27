package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;

import java.util.List;

public interface InfantService {

    String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs);

    List<InfantRegisterDTO> getInfantDetails(GetBenRequestHandler dto);
}
