package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AHDForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AHDFormRepo extends JpaRepository<AHDForm, Integer> {
    // You can add custom query methods here if needed
}