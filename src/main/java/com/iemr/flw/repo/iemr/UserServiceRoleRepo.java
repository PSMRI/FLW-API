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


    @Query(value = """
    SELECT UserID
    FROM db_iemr.v_userservicerolemapping
    WHERE UserName = :userName
      AND UserServciceRoleDeleted = 0
    LIMIT 1
    """, nativeQuery = true)
    Integer getUserIdByName(@Param("userName") String userName);

    @Query(value = """
    SELECT UserName
    FROM db_iemr.v_userservicerolemapping
    WHERE UserID = :userId
      AND UserServciceRoleDeleted = 0
    LIMIT 1
    """, nativeQuery = true)
    String getUserNamedByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT d.DistrictID, d.DistrictName FROM m_districtblock db " +
            "JOIN m_district d ON d.DistrictID = db.DistrictID " +
            "WHERE db.BlockID = :blockId LIMIT 1", nativeQuery = true)
    List<Object[]> getDistrictByBlockId(@Param("blockId") Integer blockId);

    // Stop TB / Nikshay — additive only. Reads m_userservicerolemapping directly
    // (NOT the shared v_userservicerolemapping view) so the view stays untouched
    // and no other service line's query is affected. Returns nothing for any
    // user whose rows don't have NikshayTUID set (i.e. every non-Stop-TB user).
    // NikshayTUID/NikshayFacilityID are TEXT columns holding a comma-joined
    // list of IDs per row (e.g. "12,45,78"), not a single ID, so the TU/Facility
    // master tables are joined with FIND_IN_SET rather than plain equality —
    // an equality join here would silently match only the first ID in the list
    // (MySQL casts "12,45" to 12 for numeric comparison) and drop the rest.
    @Query(value = "SELECT " +
            "GROUP_CONCAT(DISTINCT usrm.NikshayTUID ORDER BY usrm.NikshayTUID), " +
            "GROUP_CONCAT(DISTINCT nt.TUName ORDER BY nt.NikshayTUID), " +
            "GROUP_CONCAT(DISTINCT usrm.NikshayFacilityID ORDER BY usrm.NikshayFacilityID), " +
            "GROUP_CONCAT(DISTINCT nf.FacilityName ORDER BY nf.NikshayFacilityID) " +
            "FROM m_userservicerolemapping usrm " +
            "LEFT JOIN m_nikshay_tu nt ON FIND_IN_SET(nt.NikshayTUID, usrm.NikshayTUID) > 0 " +
            "LEFT JOIN m_nikshay_facility nf ON FIND_IN_SET(nf.NikshayFacilityID, usrm.NikshayFacilityID) > 0 " +
            "WHERE usrm.UserID = :userId AND usrm.ProviderServiceMapID = :providerServiceMapId " +
            "AND usrm.Deleted = false AND usrm.NikshayTUID IS NOT NULL",
            nativeQuery = true)
    List<Object[]> getNikshayLocationScope(@Param("userId") Integer userId,
                                            @Param("providerServiceMapId") Integer providerServiceMapId);

}
