package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.SammelanAttachment;
import com.iemr.flw.domain.iemr.SammelanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SammelanAttachmentRepository extends JpaRepository<SammelanAttachment, Long> {

    List<SammelanAttachment> findBySammelanRecord(SammelanRecord record);
}
