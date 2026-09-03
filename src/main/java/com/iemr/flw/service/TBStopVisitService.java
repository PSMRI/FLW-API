package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.BenVisitDetail;

public interface TBStopVisitService {

    BenVisitDetail getOrCreateVisitForToday(Long beneficiaryRegID, Integer providerServiceMapID,
                                             String createdBy, Integer vanID, Integer parkingPlaceID);
}
