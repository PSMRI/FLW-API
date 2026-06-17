package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.TBStopVisit;

public interface TBStopVisitService {

    /**
     * Returns today's visit row for this beneficiary, creating one (with visitNo = previous
     * count + 1) if none exists yet for the current calendar day. No mobile change required —
     * "new visit" is inferred purely from the save timestamp, same pattern MMU uses
     * (count existing visits, increment, insert-only — never update an existing visit row).
     */
    TBStopVisit getOrCreateVisitForToday(Long beneficiaryRegID, Integer providerServiceMapID, String createdBy,
                                          Integer vanID, Integer parkingPlaceID);
}
