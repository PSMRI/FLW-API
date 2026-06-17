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
package com.iemr.flw.domain.iemr;

import com.iemr.flw.masterEnum.ValidationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Server-side validation rule for a question.
 * validationType: MAX_LENGTH | MIN_DATE | MAX_DATE | REGEX | MANDATORY_IF
 */
@Entity
@Table(name = "t_question_validation", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "validationId")
    private Long validationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", nullable = false)
    @ToString.Exclude
    private SectionQuestion sectionQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "validationType", nullable = false, length = 30)
    private ValidationType validationType;

    /** e.g. "500" for MAX_LENGTH, "TODAY" for MAX_DATE, a regex pattern for REGEX. */
    @Column(name = "validationParam", length = 255)
    private String validationParam;

    @Column(name = "errorMessage", nullable = false, length = 500)
    private String errorMessage;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
