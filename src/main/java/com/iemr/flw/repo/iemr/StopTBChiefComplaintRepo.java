package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBChiefComplaintMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTBChiefComplaintRepo extends JpaRepository<StopTBChiefComplaintMaster, Integer> {

    List<StopTBChiefComplaintMaster> findByDeletedFalseOrDeletedIsNull();
}
