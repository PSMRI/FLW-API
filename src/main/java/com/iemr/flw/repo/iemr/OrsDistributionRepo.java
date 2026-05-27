package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.OrsDistribution;
import com.iemr.flw.domain.iemr.SamVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrsDistributionRepo extends JpaRepository<OrsDistribution,Long> {

    List<OrsDistribution> findByUserId(Integer ashaId);
}
