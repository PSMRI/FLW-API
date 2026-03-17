package com.iemr.flw.service.incentiveLogicRespo;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;

import java.util.Date;

public interface IncentiveLogicForFamilyPlaningService {

    // Family Planning - Gap Incentives
    IncentiveActivityRecord incentiveForMarriageToFirstChildGap(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForFirstToSecondChildGap(Long benId, Date startDate, Date endDate, Integer userId);

    // Kit Distribution
    IncentiveActivityRecord incentiveForKitDistribution(Long benId, Date startDate, Date endDate, Integer userId);

    // Antara (MPA doses)
    IncentiveActivityRecord incentiveForAntaraDose1(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForAntaraDose2(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForAntaraDose3(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForAntaraDose4(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForAntaraDose5(Long benId, Date startDate, Date endDate, Integer userId);

    // Sterilization
    IncentiveActivityRecord incentiveForMaleSterilization(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForFemaleSterilization(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForMiniLap(Long benId, Date startDate, Date endDate, Integer userId);

    // Temporary Methods
    IncentiveActivityRecord incentiveForCondom(Long benId, Date startDate, Date endDate, Integer userId);
    IncentiveActivityRecord incentiveForCopperT(Long benId, Date startDate, Date endDate, Integer userId);

    // Limiting Methods
    IncentiveActivityRecord incentiveForLimitTwoChildren(Long benId, Date startDate, Date endDate, Integer userId);
}
