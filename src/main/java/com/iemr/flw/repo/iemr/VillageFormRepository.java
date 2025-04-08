package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.VillageFormEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VillageFormRepository extends JpaRepository<VillageFormEntry, Long> {
}