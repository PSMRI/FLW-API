package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.repo.iemr.BenVisitDetailsRepo;
import com.iemr.flw.service.TBStopVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class TBStopVisitServiceImpl implements TBStopVisitService {

    @Autowired
    private BenVisitDetailsRepo benVisitDetailsRepo;

    @Override
    @Transactional
    public BenVisitDetail getOrCreateVisitForToday(Long beneficiaryRegID, Integer providerServiceMapID,
                                                    String createdBy, Integer vanID, Integer parkingPlaceID) {
        LocalDate today = LocalDate.now();
        Timestamp dayStart = Timestamp.valueOf(today.atStartOfDay());
        Timestamp dayEnd   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        BenVisitDetail existing = benVisitDetailsRepo.findStopTBVisitForToday(beneficiaryRegID, dayStart, dayEnd);
        if (existing != null) return existing;

        Integer visitCount = benVisitDetailsRepo.countStopTBVisits(beneficiaryRegID);
        short visitNo = (short) ((visitCount != null ? visitCount : 0) + 1);

        BenVisitDetail visit = new BenVisitDetail();
        visit.setBeneficiaryRegId(beneficiaryRegID);
        visit.setVisitNo(visitNo);
        visit.setVisitCategory("Stop TB");
        visit.setVisitReason("Screening");
        visit.setVisitDateTime(new Timestamp(System.currentTimeMillis()));
        visit.setProviderServiceMapID(providerServiceMapID);
        visit.setCreatedBy(createdBy);
        visit.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        visit.setVanId(vanID);
        visit.setParkingPlaceId(parkingPlaceID);
        visit.setDeleted(false);
        visit.setProcessed("N");
        visit = benVisitDetailsRepo.save(visit);

        // VisitCode: sessionID(1) + vanID(5-digit) + benVisitId(8-digit) — MMU formula
        long visitCode = generateVisitCode(visit.getBenVisitId(), vanID != null ? vanID : 1);
        visit.setVisitCode(visitCode);
        return benVisitDetailsRepo.save(visit);
    }

    private long generateVisitCode(long visitId, int vanID) {
        String vanStr   = String.format("%05d", vanID);
        String visitStr = String.format("%08d", visitId);
        return Long.parseLong("1" + vanStr + visitStr);
    }
}
