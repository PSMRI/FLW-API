package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityLangMapping;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Repository
public interface IncentivesRepo extends JpaRepository<IncentiveActivity, Long> {

    @Query("select inc from IncentiveActivity inc where inc.name = :name and inc.group = :group and inc.isDeleted = false")
    IncentiveActivity findIncentiveMasterByNameAndGroup(@Param("name") String name, @Param("group") String group);

    @Query("SELECT a FROM IncentiveActivity a " +
            "WHERE a.id = :activityId AND " +
            "((:isActivity = true AND a.group = 'ACTIVITY') OR (:isActivity = false AND a.group <> 'ACTIVITY'))")
    IncentiveActivity findIncentiveMasterById(
            @Param("activityId") Long activityId,
            @Param("isActivity") boolean isActivity
    );


    @Query("select record from IncentiveActivityRecord record where record.activityId = :id and record.createdDate = :createdDate and record.benId = :benId")
    IncentiveActivityRecord findRecordByActivityIdCreatedDateBenId(@Param("id") Long id, @Param("createdDate") Timestamp createdDate, @Param("benId") Long benId);

    List<IncentiveActivity> findByGroupAndIsDeleted(String group, Boolean isDeleted);

    List<IncentiveActivity> findByGroupNotAndIsDeleted(String group, Boolean isDeleted);

    // IncentivesRepo — replaces N calls to findIncentiveMasterById()
    @Query("SELECT i.id FROM IncentiveActivity i WHERE i.id IN :ids AND i.isDeleted = false AND " +
            "(:isCG = true AND i.group = 'ACTIVITY' OR :isCG = false AND i.group != 'ACTIVITY')")
    Set<Long> findValidActivityIds(@Param("ids") List<Long> ids, @Param("isCG") boolean isCG);}
