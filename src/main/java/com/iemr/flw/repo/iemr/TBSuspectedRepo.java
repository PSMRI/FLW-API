package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBSuspected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TBSuspectedRepo extends JpaRepository<TBSuspected, Long> {

    @Query(value = "SELECT * FROM TBSuspected tbs WHERE tbs.benId = :benId", nativeQuery = true)
    List<TBSuspected> getByBenId(Long pwrId);

}
