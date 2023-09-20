package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PNCCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PNCCareRepo extends JpaRepository<PNCCare, Long> {
}
