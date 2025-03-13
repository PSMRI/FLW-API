package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.M_User;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeMasterInter {
    public M_User getUserDetails(Integer userID);

}
