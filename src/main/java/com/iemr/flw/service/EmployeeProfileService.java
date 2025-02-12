package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.M_User;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeProfileService {

    M_User saveEditData(M_User editdata);
    M_User getProfileData(Integer UserID);

}
