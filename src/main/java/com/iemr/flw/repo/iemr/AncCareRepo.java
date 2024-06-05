package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AncCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AncCareRepo extends JpaRepository<AncCare, Long> {
}
