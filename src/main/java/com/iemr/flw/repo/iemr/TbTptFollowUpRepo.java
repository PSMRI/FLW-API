package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TbReferralFollowUp;
import com.iemr.flw.domain.iemr.TbTptFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbTptFollowUpRepo extends JpaRepository<TbTptFollowUp,Long> {
    List<TbTptFollowUp> findByUserId(Integer userId);
}
