package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.UserServiceRole;
import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserServiceRoleRepo extends JpaRepository<UserServiceRole, Integer> {

    @Query("SELECT new com.iemr.flw.dto.iemr.UserServiceRoleDTO(u.userId, u.name, u.userName, u.stateId, u.stateName, u.workingDistrictId," +
            "u.workingDistrictName, u.serviceProviderId, u.roleId, u.roleName, u.providerServiceMapId, u.blockid, u.blockname, u.villageid, " +
            "u.villagename) FROM UserServiceRole u where u.userId = :userId and u.userServciceRoleDeleted = false and u.userDeleted = false")
    List<UserServiceRoleDTO> getUserRole(@Param("userId") Integer userId);


    @Query("select u.userId from UserServiceRole u where u.userName = :userName and u.userServciceRoleDeleted = false")
    Integer getUserIdByName(@Param("userName") String userName);

    @Query("select u.userName from UserServiceRole u where u.userId = :userId and u.userServciceRoleDeleted = false")
    String  getUserNamedByUserId(@Param("userId") Integer userId);

}
