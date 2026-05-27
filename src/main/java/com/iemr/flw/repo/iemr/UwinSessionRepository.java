package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.UwinSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UwinSessionRepository extends JpaRepository<UwinSession, Long> {
    List<UwinSession> findByAshaId(Integer ashaId);
}
