package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenAnthropometryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenAnthropometryRepo extends JpaRepository<BenAnthropometryDetail, Long> {
}
