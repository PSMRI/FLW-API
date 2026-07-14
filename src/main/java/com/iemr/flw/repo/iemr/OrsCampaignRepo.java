package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.CampaignOrs;
import com.iemr.flw.dto.iemr.OrsCampaignDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrsCampaignRepo extends JpaRepository<CampaignOrs,Long> {
    Page<CampaignOrs> findByUserId(Integer userId, Pageable pageable);
}
