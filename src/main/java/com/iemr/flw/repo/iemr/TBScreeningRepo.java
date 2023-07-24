package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TBScreeningRepo extends JpaRepository<TBScreening, Long> {

    @Query(value = "SELECT * FROM TBScreening tbs WHERE tbs.benId = :benId", nativeQuery = true)
    List<TBScreening> getByBenId(Long benId);

    @Query(value = "SELECT * FROM TBScreening tbs WHERE tbs.userId = :userId", nativeQuery = true)
    List<TBScreening> getByUserId(Integer userId);
}
