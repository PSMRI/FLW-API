package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.M_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProfileRepo extends JpaRepository<M_User,Integer> {
    M_User  findUserByUserID(Integer userID);

}
