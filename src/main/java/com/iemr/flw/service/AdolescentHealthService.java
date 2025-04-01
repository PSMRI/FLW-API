package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface AdolescentHealthService {
    String saveAll(AdolescentHealthDTO adolescentHealthDTO);
}
