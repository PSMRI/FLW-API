package com.iemr.flw.service.impl;

import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.iemr.ANCVisit;
import com.iemr.flw.domain.iemr.User;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.ANCVisitRepo;
import com.iemr.flw.service.EmployeeMasterInter;
import com.iemr.flw.utils.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class SmsSchedulerService {
    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private SMSServiceImpl smsServiceImpl;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    @Autowired
    EmployeeMasterInter employeeMasterInter;

    @Autowired
    private ANCVisitRepo ancVisitRepo;

    @Autowired
    private ChildCareServiceImpl childCareService;
    @Autowired
    private CookieUtil cookieUtil;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendAncReminders() {
        try {

            List<ANCVisit> ancVisitList = ancVisitRepo.findAll();
            logger.info("ANC_SMS service is start");
            ancVisitList.forEach(ancVisit -> {
                Timestamp ancDate = ancVisit.getAncDate();
                if (ancDate != null) {
                    LocalDate visitDate = ancDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate tomorrow = LocalDate.now().plusDays(1);

                    if (visitDate.equals(tomorrow)) {
                        logger.info("BenId:"+ancVisit.getBenId());

                        Optional<RMNCHBeneficiaryDetailsRmnch> beneficiaryDetailsRmnch = beneficiaryRepo.findById(ancVisit.getBenId());
                        if (beneficiaryDetailsRmnch.isPresent()) {
                            RMNCHBeneficiaryDetailsRmnch beneficiary = beneficiaryDetailsRmnch.get();

                            BigInteger vanSerialNo = beneficiaryRepo.getBenIdFromRegID(beneficiary.getBenRegId());
                            logger.info("vanSerialNo:"+vanSerialNo);

                            if(beneficiaryRepo.getContactById(vanSerialNo)!=null){

                                smsServiceImpl.sendReminderSMS(beneficiaryRepo.getContactById(vanSerialNo).getContact_number(),"ANC",tomorrow);

                            }
                        }

                    }


                }
           });

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }



    @Scheduled(cron = "0 0 9 * * *")
    public void trigerTomorrowImmunizationReminders() {
        for (User m_user : employeeMasterInter.getAllUsers()) {
            childCareService.getTomorrowImmunizationReminders(m_user.getUserID());

        }
    }


}
