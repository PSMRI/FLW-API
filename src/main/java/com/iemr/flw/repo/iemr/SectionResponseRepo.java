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
import org.springframework.data.jpa.repository.JpaRepository;
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
}
