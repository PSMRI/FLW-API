package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MaaMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaaMeetingRepository extends JpaRepository<MaaMeeting, Long> {
    List<MaaMeeting> findByAshaId(Integer ashaId);
}
