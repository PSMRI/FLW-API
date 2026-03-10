package com.iemr.flw.repo.iemr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.flw.domain.iemr.User;
@Repository
public interface EmployeeMasterRepo extends JpaRepository<User,Integer> {
    @Query("SELECT u FROM User u WHERE u.userID = :userID and u.deleted=false and u.statusID in (1, 2)")
    User findUserByUserID(@Param("userID") Integer userID);
}
