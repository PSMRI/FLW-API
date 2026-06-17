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

import com.iemr.flw.domain.iemr.SectionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for section questions.
 */
@Repository
public interface SectionQuestionRepo extends JpaRepository<SectionQuestion, Long> {

    List<SectionQuestion> findByFormSection_SectionIdOrderByDisplayOrderAsc(Long sectionId);

    /**
     * Loads all questions for a set of sections in one query.
     * JOIN FETCH ensures formSection is hydrated so callers can group by sectionId without extra queries.
     */
    @Query("SELECT q FROM SectionQuestion q JOIN FETCH q.formSection "
            + "WHERE q.formSection.sectionId IN :sectionIds ORDER BY q.displayOrder ASC")
    List<SectionQuestion> findBySectionIdsOrderByDisplayOrderAsc(@Param("sectionIds") Collection<Long> sectionIds);
}
