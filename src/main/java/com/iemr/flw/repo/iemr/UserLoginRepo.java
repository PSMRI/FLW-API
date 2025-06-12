package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepo extends CrudRepository<Users, Long> {

	@Query(" SELECT u FROM Users u WHERE u.userID = :userID AND u.Deleted = false ")
	public Users getUserByUserID(@Param("userID") Long userID);

}
