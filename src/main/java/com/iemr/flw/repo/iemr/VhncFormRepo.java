package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.VhncForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VhncFormRepo extends JpaRepository<VhncForm, Integer> {
    // You can add custom query methods here if needed
}