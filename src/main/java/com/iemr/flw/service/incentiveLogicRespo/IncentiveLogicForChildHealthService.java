package com.iemr.flw.service.incentiveLogicRespo;

import com.iemr.flw.domain.iemr.*;

import java.util.Date;

public interface IncentiveLogicForChildHealthService {

    IncentiveActivityRecord incentiveForHbncVisit(HbncVisit visit, Long benId);

    IncentiveActivityRecord sncuFollowUp(HbncVisit visit, Long benId);

    IncentiveActivityRecord childDeath(HbncVisit visit, Long benId);

    IncentiveActivityRecord hbycVisit(HbycChildVisit visit, Long benId);

    IncentiveActivityRecord hbycOrs(HbycChildVisit visit, Long benId);

    IncentiveActivityRecord orsDistribution(OrsDistribution data, Long benId, boolean isCH);

    IncentiveActivityRecord samReferral(SamVisit visit, Long benId);

}
