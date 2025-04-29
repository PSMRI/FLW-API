package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralOpdData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralOpdRepo extends JpaRepository<GeneralOpdData,Long> {
}
