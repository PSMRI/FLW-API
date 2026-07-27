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

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.masterEnum.SectionPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for form sections.
 * Each section belongs to one FormVersion via the version_id FK.
 */
@Repository
public interface FormSectionRepo extends JpaRepository<FormSection, Long> {

    List<FormSection> findByFormVersion_VersionIdOrderByDisplayOrderAsc(Long versionId);

    @Query("SELECT fs.formVersion.versionId, COUNT(fs) FROM FormSection fs " +
           "WHERE fs.formVersion.versionId IN :versionIds " +
           "AND fs.sectionPhase = :sectionPhase " +
           "GROUP BY fs.formVersion.versionId")
    List<Object[]> countByFormVersion_VersionIdInAndSectionPhase(@Param("versionIds") Collection<Long> versionIds,
                                                                  @Param("sectionPhase") SectionPhase sectionPhase);
}
