package com.iemr.flw.service.impl.incentiveLogicImpl;

import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.IncentiveName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.UserService;
import com.iemr.flw.service.impl.IncentiveLogicImpl;
import com.iemr.flw.service.incentiveLogicRespo.IncentiveLogicForFamilyPlaningService;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class IncentiveLogicForFamilyPlaningImpl implements IncentiveLogicForFamilyPlaningService {

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
    public IncentiveActivityRecord incentiveForMarriageToFirstChildGap(
            Long benId, Date startDate, Date endDate, Integer userId) {

        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();
            if (stateCode == null) return null;

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive("MARRIAGE_1st_CHILD_GAP",
                        GroupName.FAMILY_PLANNING.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive("MARRIAGE_1st_CHILD_GAP",
                        GroupName.ACTIVITY.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            return null;

        } catch (Exception e) {
            logger.error("Marriage Gap Incentive Exception: ", e);
            return null;
        }
    }
    @Override
    public IncentiveActivityRecord incentiveForFirstToSecondChildGap(
            Long benId, Date startDate, Date endDate, Integer userId) {

        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();
            if (stateCode == null) return null;

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive("1st_2nd_CHILD_GAP",
                        GroupName.FAMILY_PLANNING.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive("1st_2nd_CHILD_GAP",
                        GroupName.ACTIVITY.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            return null;

        } catch (Exception e) {
            logger.error("Child Gap Incentive Exception: ", e);
            return null;
        }
    }
    @Override
    public IncentiveActivityRecord incentiveForKitDistribution(
            Long benId, Date startDate, Date endDate, Integer userId) {

        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();
            if (stateCode == null) return null;

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive("FP_NP_KIT",
                        GroupName.FAMILY_PLANNING.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive("FP_NP_KIT",
                        GroupName.ACTIVITY.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            return null;

        } catch (Exception e) {
            logger.error("Kit Incentive Exception: ", e);
            return null;
        }
    }


    @Override
    public IncentiveActivityRecord incentiveForAntaraDose1(Long benId, Date startDate, Date endDate, Integer userId) {
        return processIncentive("FP_ANC_MPA1", GroupName.FAMILY_PLANNING.getDisplayName(), benId, startDate, endDate, userId);
    }

    @Override
    public IncentiveActivityRecord incentiveForAntaraDose2(Long benId, Date startDate, Date endDate, Integer userId) {
        return processIncentive("FP_ANC_MPA2", GroupName.FAMILY_PLANNING.getDisplayName(), benId, startDate, endDate, userId);
    }

    @Override
    public IncentiveActivityRecord incentiveForAntaraDose3(Long benId, Date startDate, Date endDate, Integer userId) {
        return processIncentive("FP_ANC_MPA3", GroupName.FAMILY_PLANNING.getDisplayName(), benId, startDate, endDate, userId);
    }

    @Override
    public IncentiveActivityRecord incentiveForAntaraDose4(Long benId, Date startDate, Date endDate, Integer userId) {
        return processIncentive("FP_ANC_MPA4", GroupName.FAMILY_PLANNING.getDisplayName(), benId, startDate, endDate, userId);
    }

    @Override
    public IncentiveActivityRecord incentiveForAntaraDose5(Long benId, Date startDate, Date endDate, Integer userId) {
        return processIncentive("FP_ANC_MPA5", GroupName.FAMILY_PLANNING.getDisplayName(), benId, startDate, endDate, userId);
    }

    @Override
    public IncentiveActivityRecord incentiveForMaleSterilization(
            Long benId, Date startDate, Date endDate, Integer userId) {

        return processIncentive("FP_MALE_STER",
                GroupName.FAMILY_PLANNING.getDisplayName(),
                benId, startDate, endDate, userId);
    }
    @Override
    public IncentiveActivityRecord incentiveForFemaleSterilization(
            Long benId, Date startDate, Date endDate, Integer userId) {

        return processIncentive("FP_FEMALE_STER",
                GroupName.FAMILY_PLANNING.getDisplayName(),
                benId, startDate, endDate, userId);
    }
    @Override
    public IncentiveActivityRecord incentiveForMiniLap(
            Long benId, Date startDate, Date endDate, Integer userId) {

        return processIncentive("FP_MINILAP",
                GroupName.FAMILY_PLANNING.getDisplayName(),
                benId, startDate, endDate, userId);
    }
    @Override
    public IncentiveActivityRecord incentiveForCondom(
            Long benId, Date startDate, Date endDate, Integer userId) {

        return processIncentive("FP_CONDOM",
                GroupName.FAMILY_PLANNING.getDisplayName(),
                benId, startDate, endDate, userId);
    }
    @Override
    public IncentiveActivityRecord incentiveForCopperT(
            Long benId, Date startDate, Date endDate, Integer userId) {

        return processIncentive("FP_COPPER_T",
                GroupName.FAMILY_PLANNING.getDisplayName(),
                benId, startDate, endDate, userId);
    }
    @Override
    public IncentiveActivityRecord incentiveForLimitTwoChildren(
            Long benId, Date startDate, Date endDate, Integer userId) {

        try {
            Integer stateCode = userService.getUserDetail(userId).getStateId();
            if (stateCode == null) return null;

            if (stateCode.equals(StateCode.AM.getStateCode())) {
                return processIncentive("FP_LIMIT_2CHILD",
                        GroupName.FAMILY_PLANNING.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            if (stateCode.equals(StateCode.CG.getStateCode())) {
                return processIncentive("FP_LIMIT_2CHILD",
                        GroupName.ACTIVITY.getDisplayName(),
                        benId, startDate, endDate, userId);
            }

            return null;

        } catch (Exception e) {
            logger.error("Limit 2 Child Incentive Exception: ", e);
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
