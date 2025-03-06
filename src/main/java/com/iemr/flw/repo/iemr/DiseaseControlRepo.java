package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiseaseControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseControlRepo extends JpaRepository<DiseaseControl,Long> {
    Optional<DiseaseControl>  findByBenId(Long benId);
}
