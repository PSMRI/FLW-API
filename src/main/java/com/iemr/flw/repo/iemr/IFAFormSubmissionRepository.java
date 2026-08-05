package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.IFAFormSubmissionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IFAFormSubmissionRepository extends JpaRepository<IFAFormSubmissionData, Long> {

    List<IFAFormSubmissionData> findByUserId(Integer userName);
    @Query("SELECT i FROM IFAFormSubmissionData i " +
            "WHERE i.userId = :userId " +
            "AND i.visitDate = :visitDate")
    List<IFAFormSubmissionData> findTodayData(
            @Param("userId") Integer userId,
            @Param("visitDate") String visitDate);
}
