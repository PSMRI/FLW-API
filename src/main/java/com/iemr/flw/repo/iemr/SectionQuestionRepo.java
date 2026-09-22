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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for section questions.
 */
@Repository
public interface SectionQuestionRepo extends JpaRepository<SectionQuestion, Long> {

    List<SectionQuestion> findByFormSection_SectionIdOrderByDisplayOrderAsc(Long sectionId);

    Optional<SectionQuestion> findByFormSection_SectionIdAndQuestionUuid(Long sectionId, String questionUuid);

    /** Version-scoped lookup — a condition's target question may live in a different section of the same version. */
    Optional<SectionQuestion> findByFormSection_FormVersion_VersionIdAndQuestionUuid(Long versionId, String questionUuid);

    Optional<SectionQuestion> findTopByFormSection_SectionIdOrderByDisplayOrderDesc(Long sectionId);

    /** Shifts displayOrder by delta for every question in [from, to] within a section — used to make room for/close a gap around an inserted or moved sibling. */
    @Modifying
    @Query("UPDATE SectionQuestion q SET q.displayOrder = q.displayOrder + :delta " +
           "WHERE q.formSection.sectionId = :sectionId AND q.displayOrder BETWEEN :from AND :to")
    void shiftDisplayOrder(@Param("sectionId") Long sectionId, @Param("from") int from,
                           @Param("to") int to, @Param("delta") int delta);

    /**
     * Loads all questions for a set of sections in one query.
     * JOIN FETCH ensures formSection is hydrated so callers can group by sectionId without extra queries.
     * Excludes unlinked (isActive=false) questions — used only by read paths; reconciliation matching
     * uses the natural-key finders above, which must see inactive rows too.
     */
    @Query("SELECT q FROM SectionQuestion q JOIN FETCH q.formSection "
            + "WHERE q.formSection.sectionId IN :sectionIds AND q.isActive = true ORDER BY q.displayOrder ASC")
    List<SectionQuestion> findBySectionIdsOrderByDisplayOrderAsc(@Param("sectionIds") Collection<Long> sectionIds);
}
