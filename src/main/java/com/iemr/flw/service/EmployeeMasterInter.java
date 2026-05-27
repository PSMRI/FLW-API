package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeMasterInter {
    public User getUserDetails(Integer userID);

    List<User> getAllUsers();
}
