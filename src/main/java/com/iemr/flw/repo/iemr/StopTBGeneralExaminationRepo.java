package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTBGeneralExaminationRepo extends JpaRepository<StopTBGeneralExamination, Long> {

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.benRegID = :benRegID AND e.deleted = false")
    StopTBGeneralExamination findByBenRegID(@Param("benRegID") Long benRegID);

    @Query("SELECT e FROM StopTBGeneralExamination e WHERE e.providerServiceMapID = :psmId AND e.deleted = false ORDER BY e.createdDate DESC")
    List<StopTBGeneralExamination> findAllByProviderServiceMapID(@Param("psmId") Integer psmId);
}
