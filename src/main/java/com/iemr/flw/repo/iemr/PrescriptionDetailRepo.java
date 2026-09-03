package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionDetailRepo extends JpaRepository<PrescriptionDetail, Long> {
}
