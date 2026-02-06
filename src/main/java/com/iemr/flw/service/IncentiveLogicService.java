package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.stereotype.Service;

import java.util.Date;

public interface IncentiveLogicService {
    public IncentiveActivityRecord incentiveForLeprosyConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, String token);
}
