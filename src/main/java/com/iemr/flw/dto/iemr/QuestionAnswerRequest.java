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
package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single question's answer within a section answer request.
 * Exactly one of optionValue, optionValues, answerText, or answerDate should be set per question type:
 * RADIO      → optionValue (single string)
 * CHECKBOX   → optionValue (single string, e.g. "CHECKED")
 * MCQ        → optionValues (list of strings, one row saved per element)
 * TEXT/AUTO_FILL → answerText
 * DATE       → answerDate (ISO date string) or answerText
 * DISPLAY    → omit entirely (ignored by service)
 *
 * @author Piramal Swasthya
 */
@JsonIgnoreProperties(ignoreUnknown = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerRequest {

    @NotBlank(message = "questionUuid is required")
    private String questionUuid;

    /** RADIO — single selected option value. */
    private String optionValue;

    /** MCQ — list of selected option values. */
    private List<String> optionValues;

    /** TEXT / AUTO_FILL — free-text answer. */
    private String answerText;

    /** DATE — ISO date string answer. */
    private String answerDate;
}
