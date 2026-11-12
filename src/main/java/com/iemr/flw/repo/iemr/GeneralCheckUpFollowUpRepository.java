package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralCheckUpFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralCheckUpFollowUpRepository
        extends JpaRepository<GeneralCheckUpFollowUp, Long> {
    List<GeneralCheckUpFollowUp> findByAshaId(Integer userId);

    Optional<GeneralCheckUpFollowUp>
    findFirstByBenIdAndReferralReasonAndReferredOnDateGreaterThanEqualAndReferredOnDateLessThan(
            Long benId,
            String referralReason,
            LocalDate monthStart,
            LocalDate nextMonthStart);}

