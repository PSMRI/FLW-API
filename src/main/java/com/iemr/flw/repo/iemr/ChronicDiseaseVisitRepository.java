package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ChronicDiseaseVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChronicDiseaseVisitRepository
        extends JpaRepository<ChronicDiseaseVisitEntity, Long> {

    List<ChronicDiseaseVisitEntity> findByUserID(Integer ashaId);
}
