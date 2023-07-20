package com.iemr.flw.service;

import com.iemr.flw.dto.InfantRegisterDTO;

import java.util.List;

public interface InfantService {

    String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs);

    InfantRegisterDTO getInfantDetails(Long benId);
}
