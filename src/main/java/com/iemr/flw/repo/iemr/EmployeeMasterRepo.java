package com.iemr.flw.repo.iemr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iemr.flw.domain.iemr.M_User;
@Repository
public interface EmployeeMasterRepo extends JpaRepository<M_User,Integer> {
    M_User findByUserID(Integer userID);

    M_User getUserByUserID(Integer parseLong);
}
