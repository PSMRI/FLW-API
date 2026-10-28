package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralCheckUpFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralCheckUpFollowUpRepository
        extends JpaRepository<GeneralCheckUpFollowUp, Long> {
}