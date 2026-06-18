package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.TBStopVisit;
import com.iemr.flw.repo.iemr.TBStopVisitRepo;
import com.iemr.flw.service.TBStopVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class TBStopVisitServiceImpl implements TBStopVisitService {

    @Autowired
    private TBStopVisitRepo tbStopVisitRepo;

    @Override
    @Transactional
    public TBStopVisit getOrCreateVisitForToday(Long beneficiaryRegID, Integer providerServiceMapID, String createdBy,
                                                 Integer vanID, Integer parkingPlaceID) {
        LocalDate today = LocalDate.now();
        Timestamp todayStart = Timestamp.valueOf(today.atStartOfDay());
        Timestamp todayEnd = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        TBStopVisit visit = tbStopVisitRepo.findByBeneficiaryRegIDAndVisitDateBetween(beneficiaryRegID, todayStart, todayEnd);
        if (visit != null) {
            return visit;
        }

        Integer count = tbStopVisitRepo.getVisitCountForBeneficiary(beneficiaryRegID);
        visit = new TBStopVisit();
        visit.setBeneficiaryRegID(beneficiaryRegID);
        visit.setVisitNo(count != null ? count + 1 : 1);
        visit.setProviderServiceMapID(providerServiceMapID);
        visit.setCreatedBy(createdBy);
        if (vanID != null) {
            visit.setVanID(vanID);
            visit.setParkingPlaceID(parkingPlaceID);
        }
        visit.setProcessed("N");
        visit = tbStopVisitRepo.save(visit);
        tbStopVisitRepo.updateVanSerialNo(visit.getId());
        return visit;
    }
}
