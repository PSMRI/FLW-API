package com.iemr.flw.repo.identity;


import com.iemr.flw.domain.identity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface BeneficiaryRepo extends JpaRepository<RMNCHBeneficiaryDetailsRmnch, Long> {

    @Query(nativeQuery = true, value = " select UserName from db_iemr.m_user where  UserID = :userId and deleted is false ")
    String getUserName(@Param("userId") Integer userId);

    @Query(nativeQuery = true, value = " SELECT userid FROM db_iemr.m_user WHERE UserName = :userName ")
    Integer getUserIDByUserName(@Param("userName") String userName);

    @Query(value = " SELECT t FROM RMNCHMBeneficiaryaddress t WHERE DATE(t.createdDate) BETWEEN DATE(:fromDate) "
            + " AND DATE(:toDate) AND t.createdBy = :userName ")
    Page<RMNCHMBeneficiaryaddress> getBenDataWithinDates(@Param("userName") String userName,
                                                         @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate, Pageable pageable);

    @Query(value = " SELECT t FROM RMNCHMBeneficiaryaddress t WHERE t.createdBy = :userName ")
    Page<RMNCHMBeneficiaryaddress> getBenDataByUser(@Param("userName") String userName, Pageable pageable);

    @Query(value = " SELECT t FROM RMNCHMBeneficiarymapping t WHERE t.benAddressId = :addressID")
    RMNCHMBeneficiarymapping getByAddressID(@Param("addressID") BigInteger addressID);

    @Query(value = " SELECT t FROM RMNCHMBeneficiarydetail t WHERE t.id = :vanSerialNo")
    RMNCHMBeneficiarydetail getDetailsById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(value = " SELECT t FROM RMNCHMBeneficiaryAccount t WHERE t.id = :vanSerialNo")
    RMNCHMBeneficiaryAccount getAccountById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(value = " SELECT t FROM RMNCHMBeneficiaryImage t WHERE t.id = :vanSerialNo")
    RMNCHMBeneficiaryImage getImageById(@Param("vanSerialNo") Long vanSerialNo);

    @Query(value = " SELECT t FROM RMNCHMBeneficiaryaddress t WHERE t.id = :vanSerialNo")
    RMNCHMBeneficiaryaddress getAddressById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(value = " SELECT t FROM RMNCHMBeneficiarycontact t WHERE t.id = :vanSerialNo")
    RMNCHMBeneficiarycontact getContactById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(value = " SELECT t.beneficiaryID FROM RMNCHMBeneficiaryregidmapping t  WHERE t.benRegId = :benRegID ")
    BigInteger getBenIdFromRegID(@Param("benRegID") Long benRegID);

    @Query(value = " SELECT t FROM RMNCHBeneficiaryDetailsRmnch t WHERE t.BenRegId =:benRegID ")
    RMNCHBeneficiaryDetailsRmnch getDetailsByRegID(@Param("benRegID") Long benRegID);

    @Query(value = " SELECT t FROM RMNCHBornBirthDetails t WHERE t.BenRegId =:benRegID ")
    RMNCHBornBirthDetails getBornBirthByRegID(@Param("benRegID") Long benRegID);

    @Query(" SELECT t.benRegId FROM RMNCHMBeneficiaryregidmapping t  WHERE t.beneficiaryID = :benID ")
    Long getRegIDFromBenId(@Param("benID") Long benID);

    @Query(nativeQuery = true, value = " SELECT HealthIdNumber,HealthID  FROM db_iemr.m_benhealthidmapping WHERE BeneficiaryRegID = :benRegId ")
	Object[] getBenHealthIdNumber(@Param("benRegId") BigInteger benRegId);
    
    @Query(nativeQuery = true, value = " SELECT HealthID,HealthIdNumber,isNewAbha FROM db_iemr.t_healthid WHERE HealthIdNumber = :healthIdNumber ")
    ArrayList<Object[]> getBenHealthDetails(@Param("healthIdNumber") String healthIdNumber);
}
