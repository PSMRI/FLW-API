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

import com.iemr.flw.domain.iemr.OptionCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for option conditions.
 */
@Repository
public interface OptionConditionRepo extends JpaRepository<OptionCondition, Long> {

    List<OptionCondition> findByQuestionOption_OptionId(Long optionId);

    @Query("SELECT oc.targetQuestion.questionId FROM OptionCondition oc "
            + "WHERE oc.questionOption.sectionQuestion.formSection.formVersion.versionId = :versionId "
            + "AND oc.targetQuestion IS NOT NULL")
    List<Long> findTargetQuestionIdsByVersionId(@Param("versionId") Long versionId);

    /**
     * Loads all conditions for a set of options in one query.
     * JOIN FETCH ensures questionOption, targetQuestion, and targetSection are hydrated
     * so callers can group and resolve references without extra queries.
     */
    @Query("SELECT c FROM OptionCondition c JOIN FETCH c.questionOption "
            + "LEFT JOIN FETCH c.targetQuestion LEFT JOIN FETCH c.targetSection "
            + "WHERE c.questionOption.optionId IN :optionIds")
    List<OptionCondition> findByOptionIds(@Param("optionIds") Collection<Long> optionIds);
}
