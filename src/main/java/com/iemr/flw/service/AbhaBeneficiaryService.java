package com.iemr.flw.service;

import com.iemr.flw.dto.abhaBeneficiary.AbhaBeneficiaryDTO;
import com.iemr.flw.dto.iemr.AbhaRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AbhaBeneficiaryService {

    Object getBeneficiaryByAbha(AbhaRequestDTO request);
}
