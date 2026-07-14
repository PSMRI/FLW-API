package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.FilariasisCampaign;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilariasisCampaignRepo extends JpaRepository<FilariasisCampaign,Long> {
    Page<FilariasisCampaign> findByUserId(Integer userId, Pageable pageable);
}
