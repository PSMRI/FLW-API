package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

public interface IncentiveLogicService {
    public IncentiveActivityRecord incentiveForLeprosyPaucibacillaryConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveForIdentificationLeprosy(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveForLeprosyMultibacillaryConfirmed(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);

    public IncentiveActivityRecord incentiveForVhndMeeting(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);

    public IncentiveActivityRecord incentiveForClusterMeeting(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);
    public IncentiveActivityRecord incentiveForAttendingVhsnc(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForChildBirthGap(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForSecondChildGap(Long benId, Timestamp secondChildDob, Timestamp secondChildDob1, Integer userId);

    IncentiveActivityRecord incentiveForEyeSurgeyRefer(Long benId, Timestamp treatmentStartDate, Timestamp treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForMalariaFollowUp(Long benId, Timestamp treatmentStartDate, Timestamp treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForTbFollowUp(Long benId, Timestamp treatmentStartDate, Timestamp treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForTbFollowUpIsDrTb(Long benId, Timestamp treatmentStartDate, Timestamp treatmentEndDate, Integer userId);

    IncentiveActivityRecord incentiveForGiveingIFA(Long benId, Timestamp treatmentStartDate, Timestamp treatmentEndDate, Integer userId);
}
