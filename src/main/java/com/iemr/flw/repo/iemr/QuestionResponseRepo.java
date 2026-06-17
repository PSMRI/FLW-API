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

import com.iemr.flw.domain.iemr.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for question-level answers.
 *
 * @author Piramal Swasthya
 */
@Repository
public interface QuestionResponseRepo extends JpaRepository<QuestionResponse, Long> {

    List<QuestionResponse> findBySectionResponseId(Long sectionResponseId);

    List<QuestionResponse> findBySectionResponseIdIn(Collection<Long> sectionResponseIds);

    void deleteByQuestionIdAndSectionResponseId(Long questionId, Long sectionResponseId);

    void deleteBySectionResponseIdIn(Collection<Long> sectionResponseIds);
}
