package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AshaWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AshaProfileRepo extends JpaRepository<AshaWorker,Integer> {
    Optional<AshaWorker> findByEmployeeId(Integer employeeId);

    Optional<AshaWorker> findById(Long id);


}
