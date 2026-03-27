package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.EyeCheckupVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface  EyeCheckUpVisitRepo extends JpaRepository<EyeCheckupVisit,Long> {
    List<EyeCheckupVisit> findByUserId(Integer userId);
    List<EyeCheckupVisit> findByCreatedBy(String userName);
}
