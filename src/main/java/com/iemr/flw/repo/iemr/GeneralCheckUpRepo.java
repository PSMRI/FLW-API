package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralCheckUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralCheckUpRepo extends JpaRepository<GeneralCheckUp,Long> {
    List<GeneralCheckUp> findByCreatedByOrderByIdDesc(String userName);
}
