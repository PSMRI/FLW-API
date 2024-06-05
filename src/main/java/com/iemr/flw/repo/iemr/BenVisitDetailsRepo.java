package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenVisitDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenVisitDetailsRepo extends JpaRepository<BenVisitDetail, Long> {
}
