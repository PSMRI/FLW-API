package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TbReferralFollowUp;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.List;

@Repository
public interface TbReferralFollowUpRepo extends JpaRepository<TbReferralFollowUp,Long> {
    List<TbReferralFollowUp> findByUserId(Integer userId);
}
