package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SammelanService {
    /**
     * Submit a new Saas Bahu Sammelan record
     * @param dto request data
     * @return saved SammelanResponseDTO with incentive details
     */
    public  SammelanResponseDTO submitSammelan(SammelanRequestDTO sammelanRequestDTO);

    /**
     * Fetch Sammelan history for given ASHA worker
     * @param ashaId ASHA worker ID
     * @return list of past SammelanResponseDTO
     */
    List<SammelanResponseDTO> getSammelanHistory(Integer ashaId);

}
