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

import com.iemr.flw.domain.iemr.FormResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Repository for form-level responses.
 *
 * @author Piramal Swasthya
 */
@Repository
public interface FormResponseRepo extends JpaRepository<FormResponse, Long> {

    List<FormResponse> findByBeneficiaryIdAndFormId(Long beneficiaryId, Long formId);

   @Query("SELECT r.beneficiaryId FROM FormResponse r WHERE r.formId = :formId AND r.status = :status")
    List<Long> findBeneficiaryIdsByFormIdAndStatus(@Param("formId") Long formId, @Param("status") String status);

    @Query("SELECT r.beneficiaryId FROM FormResponse r WHERE r.formId = :formId AND r.status = :status " +
           "AND r.beneficiaryId IN (SELECT b.beneficiaryID FROM BenFlowStatus b WHERE b.deleted = false " +
           "AND (:villageId IS NULL OR b.villageID = :villageId) " +
           "AND (:providerServiceMapId IS NULL OR b.providerServiceMapId = :providerServiceMapId))")
    List<Long> findBeneficiaryIdsByFormIdAndStatusFiltered(@Param("formId") Long formId,
                                                            @Param("status") String status,
                                                            @Param("villageId") Integer villageId,
                                                            @Param("providerServiceMapId") Integer providerServiceMapId);

    @Query("SELECT r.beneficiaryId FROM FormResponse r " +
           "WHERE r.beneficiaryId IN :benIds " +
           "AND r.formId = :formId " +
           "AND r.status = :status")
    List<Long> findCounselledBenIds(@Param("benIds") List<Long> benIds,
                                    @Param("formId") Long formId,
                                    @Param("status") String status);

    @Query("SELECT r FROM FormResponse r "
         + "WHERE r.formId IN :formIds "
         + "AND r.status = 'SUBMITTED' "
         + "AND r.lastFollowUpAt >= :windowStart "
         + "AND r.lastFollowUpAt < :windowEnd")
    List<FormResponse> findPendingFollowUps(
            @Param("formIds") List<Long> formIds,
            @Param("windowStart") Timestamp windowStart,
            @Param("windowEnd") Timestamp windowEnd);
}
