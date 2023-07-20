package com.iemr.flw.repo;

import com.iemr.flw.domain.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TBScreeningRepo extends JpaRepository<TBScreening, Long> {

    @Query(value = "SELECT * FROM TBScreening tbs WHERE tbs.benId = :benId", nativeQuery = true)
    List<TBScreening> getByBenId(Long pwrId);

}
