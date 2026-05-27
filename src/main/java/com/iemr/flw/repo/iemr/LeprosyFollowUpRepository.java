package com.iemr.flw.repo.iemr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.flw.domain.iemr.LeprosyFollowUp;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeprosyFollowUpRepository extends JpaRepository<LeprosyFollowUp, Long> {
    Optional<LeprosyFollowUp> findByBenId(Long benId);

    // Custom queries can be added here if needed
    @Query("SELECT s FROM LeprosyFollowUp s WHERE s.createdBy = :createdBy")
    List<LeprosyFollowUp> getByCreatedBy(@Param("createdBy") String createdBy);
}
