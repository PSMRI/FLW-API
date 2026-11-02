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

import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.dto.iemr.LatestFormVersionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for form versions.
 */
@Repository
public interface FormVersionRepo extends JpaRepository<FormVersion, Long> {

    Optional<FormVersion> findByDynamicForm_FormIdAndIsLatest(Long formId, Boolean isLatest);

    Optional<FormVersion> findByDynamicForm_FormIdAndVersionNumber(Long formId, Integer versionNumber);

    Optional<FormVersion> findTopByDynamicForm_FormIdOrderByVersionNumberDesc(Long formId);

    List<FormVersion> findByDynamicForm_FormIdOrderByVersionNumberAsc(Long formId);

    Optional<FormVersion> findByDynamicForm_FormUuidAndIsLatest(String formUuid, boolean b);

    @Query("SELECT new com.iemr.flw.dto.iemr.LatestFormVersionDTO(f.formId, f.formUuid, f.formName, v.versionId, v.versionNumber) " +
           "FROM FormVersion v JOIN v.dynamicForm f " +
           "WHERE v.isLatest = true AND f.isActive = true " +
           "ORDER BY f.formId ASC")
    List<LatestFormVersionDTO> findLatestVersionOfActiveForms();
}
