package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.M_User;
import com.iemr.flw.repo.iemr.EmployeeProfileRepo;
import com.iemr.flw.service.EmployeeProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProfileImpl implements EmployeeProfileService {
    @Autowired
    EmployeeProfileRepo employeeProfileRepo;


    @Override
    public M_User saveEditData(M_User editdata) {
        return employeeProfileRepo.save(updateProfile(editdata));
    }

    @Override
    public M_User getProfileData(Integer UserID) {
        return employeeProfileRepo.findUserByUserID(UserID);
    }


    private M_User updateProfile(M_User editEmployee) {
        System.out.println(editEmployee.toString());

        M_User editdata = new M_User();
        editdata.setUserID(editEmployee.getUserID());
        editdata.setFirstName(editEmployee.getFirstName());
        editdata.setMiddleName(editEmployee.getMiddleName());
        editdata.setLastName(editEmployee.getLastName());
        editdata.setGenderID(editEmployee.getGenderID());
        editdata.setMaritalStatusID(editEmployee.getMaritalStatusID());
        editdata.setAadhaarNo(editEmployee.getAadhaarNo());
        editdata.setPAN(editEmployee.getPAN());
        editdata.setDOB(editEmployee.getDOB());
        editdata.setDOJ(editEmployee.getDOJ());
        editdata.setQualificationID(editEmployee.getQualificationID());
        editdata.setUserName(editEmployee.getUserName());
        editdata.setPassword(editEmployee.getPassword());
        editdata.setAgentID(editEmployee.getAgentID());
        editdata.setAgentPassword(editEmployee.getAgentPassword());
        editdata.setEmailID(editEmployee.getEmailID());
        editdata.setStatusID(editEmployee.getStatusID());
        editdata.setEmergencyContactPerson(editEmployee.getEmergencyContactPerson());
        editdata.setEmergencyContactNo(editEmployee.getEmergencyContactNo());
        editdata.setIsSupervisor(editEmployee.getIsSupervisor());
        editdata.setDeleted(editEmployee.getDeleted());  // Corrected line
        editdata.setModifiedBy(editEmployee.getModifiedBy());
        editdata.setLastModDate(editEmployee.getLastModDate());
        editdata.setCreatedBy(editEmployee.getCreatedBy());

        return editdata;
    }

}
