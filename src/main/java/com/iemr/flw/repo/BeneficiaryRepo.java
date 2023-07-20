package com.iemr.flw.repo;


import com.iemr.flw.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;

@Repository
public interface BeneficiaryRepo extends JpaRepository<RMNCHBeneficiaryDetailsRmnch, Long> {

    @Query(nativeQuery = true, value = " select UserName from db_iemr.m_user where  UserID = :userId and deleted is false ")
    public String getUserName(@Param("userId") Integer userId);

    @Query(nativeQuery = true, value = " SELECT userid FROM db_iemr.m_user WHERE UserName = :userName ")
    public Integer getUserIDByUserName(@Param("userName") String userName);

    @Query(" SELECT t FROM RMNCHMBeneficiaryaddress t WHERE DATE(t.createdDate) BETWEEN DATE(:fromDate) "
            + " AND DATE(:toDate) AND t.createdBy = :userName " + " AND t.VanID NOT IN (1,2,3,4,5,6,7,8,9) ")
    public Page<RMNCHMBeneficiaryaddress> getBenDataWithinDates(@Param("userName") String userName,
                                                                @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate, Pageable pageable);

    @Query(" SELECT t FROM RMNCHMBeneficiaryaddress t WHERE t.createdBy = :userName "
            + " AND t.VanID NOT IN (1,2,3,4,5,6,7,8,9) ")
    public Page<RMNCHMBeneficiaryaddress> getBenDataByUser(@Param("userName") String userName, Pageable pageable);

    @Query(" SELECT t FROM RMNCHMBeneficiarymapping t WHERE t.benAddressId = :addressID")
    public RMNCHMBeneficiarymapping getByAddressID(@Param("addressID") BigInteger addressID);

    @Query(" SELECT t FROM RMNCHMBeneficiarydetail t WHERE t.id = :vanSerialNo")
    public RMNCHMBeneficiarydetail getDetailsById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(" SELECT t FROM RMNCHMBeneficiaryAccount t WHERE t.id = :vanSerialNo")
    public RMNCHMBeneficiaryAccount getAccountById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(" SELECT t FROM RMNCHMBeneficiaryImage t WHERE t.id = :vanSerialNo")
    public RMNCHMBeneficiaryImage getImageById(@Param("vanSerialNo") Long vanSerialNo);

    @Query(" SELECT t FROM RMNCHMBeneficiaryaddress t WHERE t.id = :vanSerialNo")
    public RMNCHMBeneficiaryaddress getAddressById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(" SELECT t FROM RMNCHMBeneficiarycontact t WHERE t.id = :vanSerialNo")
    public RMNCHMBeneficiarycontact getContactById(@Param("vanSerialNo") BigInteger vanSerialNo);

    @Query(" SELECT beneficiaryID FROM RMNCHMBeneficiaryregidmapping t  WHERE t.benRegId = :benRegID ")
    public BigInteger getBenIdFromRegID(@Param("benRegID") Long benRegID);

    @Query(" SELECT t FROM RMNCHBeneficiaryDetailsRmnch t WHERE t.BenRegId =:benRegID ")
    public RMNCHBeneficiaryDetailsRmnch getDetailsByRegID(@Param("benRegID") Long benRegID);

    @Query(" SELECT t FROM RMNCHBornBirthDetails t WHERE t.BenRegId =:benRegID ")
    public RMNCHBornBirthDetails getBornBirthByRegID(@Param("benRegID") Long benRegID);
}
