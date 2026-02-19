package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
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

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    @Override
    public IncentiveActivityRecord incentiveForLeprosyPaucibacillaryConfirmed(
            Long benId,
            Date treatmentStartDate,
            Date treatmentEndDate,
            Integer userId) {

        try {
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
                        userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check NLEP_PB_TREATMENT Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord incentiveForLeprosyMultibacillaryConfirmed(
            Long benId,
            Date treatmentStartDate,
            Date treatmentEndDate,
            Integer userId) {

        try {
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
                        userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check NLEP_PB_TREATMENT Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord incentiveForVhndMeeting(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId) {
        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();

            if (stateCode == null) {
                logger.warn("State code is null for user: {}", userId);
                return null;
            }

            String activityName = "VHND_PARTICIPATION";

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check VHND_PARTICIPATION Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord incentiveForClusterMeeting(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId) {
        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();

            if (stateCode == null) {
                logger.warn("State code is null for user: {}", userId);
                return null;
            }

            String activityName = "CLUSTER_MEETING";

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check CLUSTER_MEETING Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord incentiveForAttendingVhsnc(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId) {
        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();

            if (stateCode == null) {
                logger.warn("State code is null for user: {}", userId);
                return null;
            }

            String activityName = "VHSNC_MEETING";

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            // state not supported
            logger.info("No incentive mapping for stateCode: {}", stateCode);
            return null;

        } catch (Exception e) {
            logger.error("Check VHSNC_MEETING Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord incentiveForIdentificationLeprosy(Long benId, Date treatmentStartDate, Date treatmentEndDate, Integer userId) {
        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();

            if (stateCode == null) {
                logger.warn("State code is null for user: {}", userId);
                return null;
            }

            String activityName = "NLEP_CASE_DETECTION";

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.UMBRELLA_PROGRAMMES.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive(
                        activityName,
                        GroupName.ACTIVITY.getDisplayName(),
                        benId,
                        treatmentStartDate,
                        treatmentEndDate,
                        userId);
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
                                                     Date startDate, Date endDate, Integer userId) {
        try {
            IncentiveActivity activity = incentivesRepo.findIncentiveMasterByNameAndGroup(activityName, groupName);

            if (activity == null) {
                logger.info("No IncentiveActivity found for name: {} and group: {}", activityName, groupName);
                return null ;
            }

         return   saveIncentive(activity, benId, startDate, endDate, userId);

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
            Integer userId) {

        try {

            if (activity == null || benId == null || startDate == null || endDate == null) {
                logger.warn("Invalid input for saving incentive");
                return null;
            }

            Timestamp startTimestamp = new Timestamp(startDate.getTime());
            Timestamp endTimestamp = new Timestamp(endDate.getTime());


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
            record.setCreatedBy(userServiceRoleRepo.getUserNamedByUserId(userId));
            record.setUpdatedBy(userServiceRoleRepo.getUserNamedByUserId(userId));
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setName(activity.getName());
            record.setAmount(Long.valueOf(activity.getRate()));

            record = incentiveRecordRepo.save(record);

            logger.info("Incentive saved successfully for benId: {}", benId);

            return record;

        } catch (Exception e) {
            logger.error("Error Incentive Exception: ", e);
            return null;
        }
    }

}
