package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.EmployeeMasterRepo;
import com.iemr.flw.service.EmployeeMasterInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeMasterImpl  implements EmployeeMasterInter {
    @Autowired
    EmployeeMasterRepo  employeeMasterRepo;

    @Override
    public M_User getUserDetails(Integer userID) {
        return employeeMasterRepo.findByUserID(userID);
    }
}
