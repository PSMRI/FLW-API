package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.domain.iemr.PulsePolioCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PulsePolioCampaignRepo extends JpaRepository<PulsePolioCampaign,Long> {
    Page<PulsePolioCampaign> findByUserId(Integer userId, Pageable pageable);
}
