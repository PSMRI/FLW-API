package com.iemr.flw.service;

import com.iemr.flw.dto.EligibleCoupleDTO;
import com.iemr.flw.dto.EligibleCoupleTrackingDTO;

import java.util.List;

public interface CoupleService {

    String registerEligibleCouple(List<EligibleCoupleDTO> eligibleCoupleDTOs);

    String registerEligibleCoupleTracking(List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs);

    EligibleCoupleDTO getEligibleCouple(Long benId);

    List<EligibleCoupleTrackingDTO> getEligibleCoupleTracking(Long ecrId);

}
