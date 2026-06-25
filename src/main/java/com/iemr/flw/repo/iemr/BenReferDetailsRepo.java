/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.flw.repo.iemr;


import com.iemr.flw.domain.iemr.BenReferDetails;

import io.swagger.v3.oas.annotations.info.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BenReferDetailsRepo extends JpaRepository<BenReferDetails, Long> {


	@Query("SELECT b.referredToInstituteName FROM BenReferDetails b WHERE b.beneficiaryRegID = :benRegID")
	String findInstituteNameByBeneficiaryRegID(@Param("benRegID") Long benRegID);


	@Query("SELECT b.referralReason FROM BenReferDetails b WHERE b.beneficiaryRegID = :benRegID")
	String findReasonByBeneficiaryRegID(@Param("benRegID") Long benRegID);

	List<BenReferDetails> findByCreatedBy(String userName);

	@Query("""
       SELECT COUNT(b)
       FROM BenReferDetails b
       WHERE b.createdBy = :userName
       AND FUNCTION('MONTH', b.createdDate) = :month
       AND FUNCTION('YEAR', b.createdDate) = :year
       """)
	Long countMonthlyReferrals(
			@Param("userName") String userName,
			@Param("month") Integer month,
			@Param("year") Integer year);

}
