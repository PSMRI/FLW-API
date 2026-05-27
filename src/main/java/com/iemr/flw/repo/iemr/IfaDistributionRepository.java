package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.IfaDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IfaDistributionRepository extends JpaRepository<IfaDistribution, Long> {
    List<IfaDistribution> findByUserId(Integer ashaId);
}
