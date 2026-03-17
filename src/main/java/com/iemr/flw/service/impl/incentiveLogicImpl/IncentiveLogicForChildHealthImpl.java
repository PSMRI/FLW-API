package com.iemr.flw.service.impl.incentiveLogicImpl;

import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.incentiveLogicRespo.IncentiveLogicForChildHealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class IncentiveLogicForChildHealthImpl implements IncentiveLogicForChildHealthService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveLogicForChildHealthImpl.class);

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;


    private String getGroupByState(Integer stateId) {
        if (stateId.equals(StateCode.AM.getStateCode())) {
            return GroupName.CHILD_HEALTH.getDisplayName();
        } else if (stateId.equals(StateCode.CG.getStateCode())) {
            return GroupName.ACTIVITY.getDisplayName();
        }
        return null;
    }

    private Timestamp parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    // ================= HBNC =================

    @Override
    public IncentiveActivityRecord incentiveForHbncVisit(HbncVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreatedBy());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreatedBy());

            Timestamp visitDate = parseDate(visit.getVisit_date());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("HBNC_VISIT", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("HBNC Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord sncuFollowUp(HbncVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreatedBy());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreatedBy());

            Timestamp visitDate = parseDate(visit.getVisit_date());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("SNCU_FOLLOWUP", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("SNCU Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord childDeath(HbncVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreatedBy());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreatedBy());

            Timestamp visitDate = parseDate(visit.getVisit_date());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("CHILD_DEATH", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("Child Death Incentive Exception: ", e);
            return null;
        }
    }

    // ================= HBYC =================

    @Override
    public IncentiveActivityRecord hbycVisit(HbycChildVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreated_by());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreated_by());

            Timestamp visitDate = parseDate(visit.getVisit_date());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("HBYC_VISIT", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("HBYC Visit Incentive Exception: ", e);
            return null;
        }
    }

    @Override
    public IncentiveActivityRecord hbycOrs(HbycChildVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreated_by());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreated_by());

            Timestamp visitDate = parseDate(visit.getVisit_date());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("HBYC_ORS", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("HBYC ORS Incentive Exception: ", e);
            return null;
        }
    }

    // ================= ORS =================

    @Override
    public IncentiveActivityRecord orsDistribution(OrsDistribution data, Long benId, boolean isCH) {
        try {
            Integer stateId = userServiceRoleRepo.getStateIdByUserId(data.getUserId());

            Timestamp date = new Timestamp(data.getCreatedAt().getTime());

            String groupName = isCH
                    ? getGroupByState(stateId)
                    : GroupName.ACTIVITY.getDisplayName();

            if (groupName == null) return null;

            return processIncentive("ORS_DISTRIBUTION", groupName, benId, date, date, data.getUserId());

        } catch (Exception e) {
            logger.error("ORS Incentive Exception: ", e);
            return null;
        }
    }

    // ================= SAM =================

    @Override
    public IncentiveActivityRecord samReferral(SamVisit visit, Long benId) {
        try {
            Integer userId = userServiceRoleRepo.getUserIdByName(visit.getCreatedBy());
            Integer stateId = userServiceRoleRepo.getStateIdByUserName(visit.getCreatedBy());

            Timestamp visitDate = parseDate(visit.getFollowUpVisitDate());
            String groupName = getGroupByState(stateId);

            if (groupName == null) return null;

            return processIncentive("SAM_REFERRAL", groupName, benId, visitDate, visitDate, userId);

        } catch (Exception e) {
            logger.error("SAM Incentive Exception: ", e);
            return null;
        }
    }

    // ================= CORE ENGINE =================

    private IncentiveActivityRecord processIncentive(String activityName, String groupName, Long benId,
                                                     Date startDate, Date endDate, Integer userId) {
        try {
            IncentiveActivity activity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup(activityName, groupName);

            if (activity == null) {
                logger.info("No IncentiveActivity found for name: {} and group: {}", activityName, groupName);
                return null;
            }

            return saveIncentive(activity, benId, startDate, endDate, userId);

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
            Timestamp startTimestamp = new Timestamp(startDate.getTime());
            Timestamp endTimestamp = new Timestamp(endDate.getTime());

            // duplicate check
            IncentiveActivityRecord existing =
                    incentiveRecordRepo.findRecordByActivityIdCreatedDateBenId(
                            activity.getId(), startTimestamp, benId);

            if (existing != null) {
                return existing;
            }

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

            return incentiveRecordRepo.save(record);

        } catch (Exception e) {
            logger.error("Save Incentive Exception: ", e);
            return null;
        }
    }
}