package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.stereotype.Service;

import java.util.Date;

public interface IncentiveLogicService {
    public IncentiveActivityRecord incentiveForLeprosyPaucibacillaryConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveForIdentificationLeprosy(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveForLeprosyMultibacillaryConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveAssistingANCVisits(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);


}
