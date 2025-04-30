package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DewormingForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DewormingFormRepo extends JpaRepository<DewormingForm, Integer> {
    // You can add custom query methods here if needed
}