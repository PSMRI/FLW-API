package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CoupleService {


    String registerEligibleCoupleTracking(List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs);

    String getEligibleCoupleRegRecords(GetBenRequestHandler dto);

    List<EligibleCoupleTrackingDTO> getEligibleCoupleTracking(GetBenRequestHandler requestDto);

    String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs, MultipartFile kitPhoto1, MultipartFile kitPhoto2);

    String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs);

}
