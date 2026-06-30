package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenPhysicalVitalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenPhysicalVitalRepo extends JpaRepository<BenPhysicalVitalDetail, Long> {
}
