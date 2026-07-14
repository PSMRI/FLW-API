package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.NikshayVillageFacilityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NikshayVillageFacilityMappingRepo extends JpaRepository<NikshayVillageFacilityMapping, Integer> {

    @Query("SELECT m FROM NikshayVillageFacilityMapping m WHERE m.amritVillageID = :villageId")
    List<NikshayVillageFacilityMapping> findByAmritVillageID(@Param("villageId") Integer villageId);
}
