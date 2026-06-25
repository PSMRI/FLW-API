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

    @Query(value = """
    SELECT COUNT(DISTINCT cv.VaccineName)
    FROM db_iemr.t_childvaccinedetail1 cv
    JOIN db_iemr.m_immunizationservicevaccination v
         ON cv.VaccineName = v.VaccineName
    WHERE cv.BeneficiaryRegID = :benRegId
    AND v.Currentimmunizationserviceid IN (1,2,3,4,5)
    AND v.category = 'CHILD'
    """, nativeQuery = true)
    Integer getFirstYearVaccineCountForBenId(@Param("benRegId") Long benRegId);



    @Query(value = "select count(*) from db_iemr.m_immunizationservicevaccination v where v.Currentimmunizationserviceid in (1,2,3,4,5) " +
            "and v.category = 'CHILD'", nativeQuery = true)
    Integer getFirstYearVaccineCount();

    @Query(value = "select count(*) from db_iemr.t_childvaccinedetail1 cv left outer join db_iemr.m_immunizationservicevaccination v on " +
            "cv.VaccineName = v.VaccineName where cv.beneficiaryRegId = :benRegId and v.Currentimmunizationserviceid = 7 and " +
            "v.category = 'CHILD'", nativeQuery = true)
    Integer getSecondYearVaccineCountForBenId(@Param("benRegId") Long benRegId);

    @Query(value = "select count(*) from db_iemr.m_immunizationservicevaccination v where v.Currentimmunizationserviceid = 7 " +
            "and v.category = 'CHILD'", nativeQuery = true)
    Integer getSecondYearVaccineCount();

    @Query(value = "SELECT IF(EXISTS(SELECT * from db_iemr.t_childvaccinedetail1 cv left outer join db_iemr.m_immunizationservicevaccination v on " +
            "cv.VaccineName = v.VaccineName where cv.beneficiaryRegId = :benRegId and v.Currentimmunizationserviceid = 8 and v.VaccineName = " +
            "'DPT Booster-2' and v.category = 'CHILD'), 1, 0);", nativeQuery = true)
    Integer checkDptVaccinatedUser(@Param("benRegId") Long benRegId);

    @Query(value = """
    SELECT COUNT(DISTINCT cv.VaccineName)
    FROM db_iemr.t_childvaccinedetail1 cv
    WHERE cv.BeneficiaryRegID = :benRegId
    AND cv.VaccineName IN (
        'DPT Booster-1',
        'Measles & Rubella (MR)-2',
        'OPV Booster'
    )
    """, nativeQuery = true)
    Integer getEligibleSecondYearVaccines(@Param("benRegId") Long benRegId);
}
