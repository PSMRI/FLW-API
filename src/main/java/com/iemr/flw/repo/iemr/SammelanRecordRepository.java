package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.SammelanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SammelanRecordRepository extends JpaRepository<SammelanRecord, Long> {


    // Fetch history
    List<SammelanRecord> findByAshaId(Integer ashaId);
}
