package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.InfantRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface InfantRegisterRepo extends JpaRepository<InfantRegister, Long> {

    @Query(" SELECT ir FROM InfantRegister ir WHERE ir.createdBy = :userId and ir.createdDate >= :fromDate and ir.createdDate <= :toDate")
    List<InfantRegister> getInfantDetailsForUser(@Param("userId") Integer userId,
                                                 @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);
}
