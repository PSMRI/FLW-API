package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import org.springframework.stereotype.Service;

import java.util.List;
public interface AdolescentHealthService {
    String saveAll(AdolescentHealthDTO adolescentHealthDTO);
    List<AdolescentHealth> getAllAdolescentHealth(GetBenRequestHandler getBenRequestHandler);

}
