package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.IncentivePendingActivity;
import com.iemr.flw.repo.iemr.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateIncentivePendindDocService {
    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private IncentivePendingActivityRepository incentivePendingActivityRepository;

    public void updateIncentive(Long id) {

        Optional<IncentiveActivityRecord> optionalRecord = recordRepo.findById(id);

        if (optionalRecord.isPresent()) {
            IncentiveActivityRecord record = optionalRecord.get();
            record.setIsEligible(true);
            recordRepo.save(record);
        }
    }

    public void updatePendingActivity(Integer userId, Long recordId, Long activityId, Long mIncentiveId) {
        IncentivePendingActivity incentivePendingActivity = new IncentivePendingActivity();
        incentivePendingActivity.setActivityId(activityId);
        incentivePendingActivity.setRecordId(recordId);
        incentivePendingActivity.setUserId(userId);
        incentivePendingActivity.setMincentiveId(mIncentiveId);
        if (incentivePendingActivity != null) {
            incentivePendingActivityRepository.save(incentivePendingActivity);
        }

    }

}
