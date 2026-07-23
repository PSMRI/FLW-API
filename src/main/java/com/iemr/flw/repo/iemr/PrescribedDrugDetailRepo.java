package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PrescribedDrugDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescribedDrugDetailRepo extends JpaRepository<PrescribedDrugDetail, Long> {
}
