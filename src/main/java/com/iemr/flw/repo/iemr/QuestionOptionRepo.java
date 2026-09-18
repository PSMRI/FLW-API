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

import com.iemr.flw.domain.iemr.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for question options.
 */
@Repository
public interface QuestionOptionRepo extends JpaRepository<QuestionOption, Long> {

    List<QuestionOption> findBySectionQuestion_QuestionIdOrderByDisplayOrderAsc(Long questionId);

    /**
     * Loads all options for a set of questions in one query.
     * JOIN FETCH ensures sectionQuestion is hydrated so callers can group by questionId without extra queries.
     */
    @Query("SELECT o FROM QuestionOption o JOIN FETCH o.sectionQuestion "
            + "WHERE o.sectionQuestion.questionId IN :questionIds ORDER BY o.displayOrder ASC")
    List<QuestionOption> findByQuestionIdsOrderByDisplayOrderAsc(@Param("questionIds") Collection<Long> questionIds);
}
