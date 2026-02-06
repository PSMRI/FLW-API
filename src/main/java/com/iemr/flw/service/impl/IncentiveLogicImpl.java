package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class IncentiveLogicImpl implements IncentiveLogicService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveLogicImpl.class);

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public IncentiveActivityRecord incentiveForLeprosyConfirmed(
            Long benId,
            Date treatmentStartDate,
            Date treatmentEndDate,
            String token) {

        try {
            Integer userId = jwtUtil.extractUserId(token);
            Integer stateCode = userService.getUserDetail(userId).getStateId();

            if (stateCode == null) {
                logger.warn("State code is null for user: {}", userId);
                return null;
            }

            String activityName = "NLEP_PB_TREATMENT";

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.UMBRELLA_PROGRAMMES.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        token);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        token);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check Leprosy Incentive Exception: ", e);
            return null;
        }
    }


    private IncentiveActivityRecord processIncentive(String activityName, String groupName, Long benId,
                                  Date startDate, Date endDate, String token) {
        try {
            IncentiveActivity activity = incentivesRepo.findIncentiveMasterByNameAndGroup(activityName, groupName);

            if (activity == null) {
                logger.info("No IncentiveActivity found for name: {} and group: {}", activityName, groupName);
                return null ;
            }

         return   saveIncentive(activity, benId, startDate, endDate, token);

        } catch (Exception e) {
            logger.error("Process Incentive Exception: ", e);
            return null;

        }
    }

    private IncentiveActivityRecord saveIncentive(
            IncentiveActivity activity,
            Long benId,
            Date startDate,
            Date endDate,
            String token) {

        try {

            if (activity == null || benId == null || startDate == null || endDate == null) {
                logger.warn("Invalid input for saving incentive");
                return null;
            }

            Timestamp startTimestamp = new Timestamp(startDate.getTime());
            Timestamp endTimestamp = new Timestamp(endDate.getTime());

            Integer userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractUsername(token);

            // 🔍 duplicate check
            IncentiveActivityRecord existing = incentiveRecordRepo
                    .findRecordByActivityIdCreatedDateBenId(
                            activity.getId(), startTimestamp, benId);

            if (existing != null) {
                logger.info("Incentive already exists for benId: {}, activityId: {}",
                        benId, activity.getId());
                return existing;
            }

            // 🆕 create record
            IncentiveActivityRecord record = new IncentiveActivityRecord();
            record.setActivityId(activity.getId());
            record.setCreatedDate(startTimestamp);
            record.setUpdatedDate(startTimestamp);
            record.setStartDate(startTimestamp);
            record.setEndDate(endTimestamp);
            record.setCreatedBy(username);
            record.setUpdatedBy(username);
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setName(activity.getName());
            record.setAmount(Long.valueOf(activity.getRate()));

            record = incentiveRecordRepo.save(record);

            logger.info("Leprosy Incentive saved successfully for benId: {}", benId);

            return record;

        } catch (Exception e) {
            logger.error("Save Leprosy Incentive Exception: ", e);
            return null;
        }
    }

}
