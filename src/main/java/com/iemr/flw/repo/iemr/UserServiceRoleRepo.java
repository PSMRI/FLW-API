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
    // NikshayTUID/NikshayFacilityID are TEXT columns holding a comma-joined list
    // of IDs (e.g. "12,45,78"), not a single ID. Name resolution against
    // m_nikshay_tu/m_nikshay_facility is done in Java (see UserServiceImpl) via
    // an indexed IN(...) lookup on their primary keys, instead of joining here
    // with FIND_IN_SET — FIND_IN_SET can't use an index on either side, so it
    // forces a full scan of the master table per call (measured: ~60s against
    // m_nikshay_facility's 300k+ rows for a user with ~100 mapped facilities).
    @Query(value = "SELECT usrm.NikshayTUID, usrm.NikshayFacilityID, usrm.DistrictID " +
            "FROM m_userservicerolemapping usrm " +
            "WHERE usrm.UserID = :userId AND usrm.ProviderServiceMapID = :providerServiceMapId " +
            "AND usrm.Deleted = false AND usrm.NikshayTUID IS NOT NULL",
            nativeQuery = true)
    List<Object[]> getNikshayMappingRows(@Param("userId") Integer userId,
                                          @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query(value = "SELECT NikshayTUID, TUName FROM m_nikshay_tu WHERE NikshayTUID IN (:ids)", nativeQuery = true)
    List<Object[]> findNikshayTuNames(@Param("ids") java.util.Collection<Integer> ids);

    @Query(value = "SELECT NikshayFacilityID, FacilityName FROM m_nikshay_facility WHERE NikshayFacilityID IN (:ids)", nativeQuery = true)
    List<Object[]> findNikshayFacilityNames(@Param("ids") java.util.Collection<Integer> ids);

    // DistrictID on m_userservicerolemapping is a Nikshay district ID, not an
    // AMRIT one (see setWorkLocationObject in Admin-UI) - resolving its name
    // against m_nikshay_district (the same ID space) is safe, unlike joining it
    // against AMRIT's own m_district, which would silently match whatever AMRIT
    // district happens to share that same numeric ID by coincidence.
    @Query(value = "SELECT DistrictName FROM m_nikshay_district WHERE NikshayDistrictID = :id", nativeQuery = true)
    String findNikshayDistrictName(@Param("id") Integer id);

}
