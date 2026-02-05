package com.iemr.flw.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface IncentiveLogicService {
    public void  incentiveForLeprosyConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, String token);
}
