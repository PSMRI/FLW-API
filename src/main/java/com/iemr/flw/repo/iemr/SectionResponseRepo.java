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

import com.iemr.flw.domain.iemr.SectionResponse;
import com.iemr.flw.masterEnum.SectionPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for section-level responses.
 *
 * @author Piramal Swasthya
 */
@Repository
public interface SectionResponseRepo extends JpaRepository<SectionResponse, Long> {

    List<SectionResponse> findByResponseId(Long responseId);

    Optional<SectionResponse> findByResponseIdAndSectionId(Long responseId, Long sectionId);

    List<SectionResponse> findByResponseIdIn(Collection<Long> responseIds);

    void deleteByResponseId(Long responseId);

    @Query("SELECT sr.responseId, COUNT(sr) FROM SectionResponse sr, FormSection fs " +
           "WHERE sr.sectionId = fs.sectionId " +
           "AND fs.sectionPhase = :sectionPhase " +
           "AND sr.responseId IN :responseIds GROUP BY sr.responseId")
    List<Object[]> countByResponseIdInAndSectionPhase(@Param("responseIds") Collection<Long> responseIds,
                                                       @Param("sectionPhase") SectionPhase sectionPhase);

    // vanSerialNo is never set at creation time (no van-context available yet on a plain
    // .save()) - called right after save() once the auto-generated sectionResponseId is
    // known, matching the "VanSerialNo == own PK" convention used across this codebase.
    // A targeted UPDATE, not a second full-entity save - see BeneficiaryServiceImpl/
    // CommonBenStatusFlowServiceImpl for why a full re-save is the wrong fix here.
    @Modifying
    @Query("UPDATE SectionResponse s SET s.vanSerialNo = :vanSerialNo WHERE s.sectionResponseId = :id")
    void updateVanSerialNo(@Param("id") Long id, @Param("vanSerialNo") Long vanSerialNo);
}
