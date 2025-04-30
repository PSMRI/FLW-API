package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.PHCReviewForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PHCReviewFormRepo extends JpaRepository<PHCReviewForm, Integer> {
    // You can add custom query methods here if needed
}