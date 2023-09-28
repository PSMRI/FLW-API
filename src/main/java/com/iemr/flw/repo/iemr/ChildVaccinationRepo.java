package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ChildVaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ChildVaccinationRepo extends JpaRepository<ChildVaccination, Long> {

    @Query(value = "SELECT cv FROM  ChildVaccination cv WHERE cv.createdBy = :userId and cv.createdDate >= :fromDate and cv.createdDate <= :toDate")
    List<ChildVaccination> getChildVaccinationDetails(@Param("userId") String userId,
                               @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    ChildVaccination findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(Long benRegId, Timestamp createdDate, String vaccine);
}
