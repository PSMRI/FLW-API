package com.iemr.flw.repo;

import com.iemr.flw.domain.UserServiceRole;
import com.iemr.flw.dto.UserServiceRoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserServiceRoleRepo extends JpaRepository<UserServiceRole, Integer> {

    @Query("SELECT new com.iemr.flw.dto.UserServiceRoleDTO(u.userId, u.name, u.userName, u.stateId, u.stateName, u.workingDistrictId," +
            "u.workingDistrictName, u.serviceProviderId, u.roleId, u.roleName, u.providerServiceMapId, u.blockid," +
            "u.blockname, u.villageid, u.villagename) FROM UserServiceRole u where u.userId = :userId and u.roleId = :roleId")
    UserServiceRoleDTO getUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
