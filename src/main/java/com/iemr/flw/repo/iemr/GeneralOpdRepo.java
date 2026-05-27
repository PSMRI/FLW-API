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
* @@ -0,0 +1,212 @@
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.GeneralOpdData;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface GeneralOpdRepo extends JpaRepository<GeneralOpdData,Long> {

    @Query(value = """
    SELECT ibfo.* 
    FROM db_iemr.i_ben_flow_outreach ibfo
    JOIN db_identity.m_beneficiaryregidmapping mbrm
      ON ibfo.beneficiary_reg_id = mbrm.BenRegId
    WHERE ibfo.visit_category = 'General OPD'
      AND ibfo.beneficiary_visit_code <> 0
      AND ibfo.villageID = :villageID
      AND ibfo.created_by =:userName
      AND (ibfo.deleted IS NULL OR ibfo.deleted = 0)
      AND (mbrm.Deleted IS NULL OR mbrm.Deleted = 0)
    ORDER BY ibfo.created_date DESC
    """,
            countQuery = """
    SELECT COUNT(*) 
    FROM db_iemr.i_ben_flow_outreach ibfo
    JOIN db_identity.m_beneficiaryregidmapping mbrm
      ON ibfo.beneficiary_reg_id = mbrm.BenRegId
    WHERE ibfo.visit_category = 'General OPD'
      AND ibfo.beneficiary_visit_code <> 0
      AND ibfo.villageID = :villageID
      AND (ibfo.deleted IS NULL OR ibfo.deleted = 0)
      AND (mbrm.Deleted IS NULL OR mbrm.Deleted = 0)
    """,
            nativeQuery = true)
    Page<GeneralOpdData> findFilteredOutreachData(
            @Param("villageID") Integer villageID,
            Pageable pageable,@Param("userName") String  userName);

}
