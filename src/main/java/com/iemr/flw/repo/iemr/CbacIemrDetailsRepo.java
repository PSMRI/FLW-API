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

import com.iemr.flw.domain.iemr.CbacDetailsImer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CbacIemrDetailsRepo extends CrudRepository<CbacDetailsImer, Long> {
	
	public CbacDetailsImer findByBeneficiaryRegIdAndVisitCode(Long beneficiaryRegId, Long visitCode);

	List<CbacDetailsImer> findByCreatedBy(String userName);

	@Query(value = "SELECT BeneficiaryID FROM db_identity.m_beneficiaryregidmapping " +
			"WHERE BenRegId = :benRegId AND Deleted = 0", nativeQuery = true)
	Long getBeneficiaryId(@Param("benRegId") Long benRegId);

}
