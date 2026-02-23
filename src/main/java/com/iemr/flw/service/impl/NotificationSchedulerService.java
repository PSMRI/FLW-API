package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.User;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.utils.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationSchedulerService {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

   
    @Autowired
    private NotificationService notificationService;
    @Autowired
    EmployeeMasterInter employeeMasterInter;

    @Autowired
   private ChildCareServiceImpl childCareService;
    @Autowired
    private CookieUtil cookieUtil;
    
    @Scheduled(cron = "0 0 9 * * *") // every day at 9 AM
    public void triggerAncRemindersForAllAsha() {

//        HttpServletRequest requestHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//                .getRequest();
//        String jwtTokenFromCookie = cookieUtil.getJwtTokenFromCookie(requestHeader);
//        for(M_User m_user: employeeMasterInter.getAllUsers()){
//            maternalHealthService.sendAncDueTomorrowNotifications(String.valueOf(m_user.getUserID()));
//
//        }
        logger.info("triggerAncRemindersForAllAsha Service start");
        String ancType = "ANC!"; // ANC1, ANC2, etc.
        String body = "Reminder: Scheduled ANC check-up (" + ancType + ") is due tomorrow.";
        String redirectPath = "/work-plan/anc/" + ancType.toLowerCase();
        String appType = "FLW_APP"; // or "ASHAA_APP", based on user type
//        String topic = "ANC"+963; // or some user/topic identifier
        String topic = "All"; // or some user/topic identifier
        String title = "ANC Reminder";

        notificationService.sendNotification(
                appType,
                topic,
                title,
                body,
                redirectPath
        );

    }

    @Scheduled(cron = "0 0 9 * * *")
    public void trigerTomorrowImmunizationReminders() {
        for(User m_user: employeeMasterInter.getAllUsers()){
            childCareService.getTomorrowImmunizationReminders(m_user.getUserID());

        }
    }

    
}
