package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PhyGeneralExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhyGeneralExaminationRepo extends JpaRepository<PhyGeneralExamination, Long> {
}
