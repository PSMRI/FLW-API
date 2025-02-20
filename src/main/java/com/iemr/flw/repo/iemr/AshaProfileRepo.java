package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AshaWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AshaProfileRepo extends JpaRepository<AshaWorker,Integer> {

    AshaWorker findByEmployeeId(Integer employeeId);

}
