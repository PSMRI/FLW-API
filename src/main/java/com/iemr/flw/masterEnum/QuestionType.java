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
package com.iemr.flw.masterEnum;

/**
 * Exhaustive set of question input types for dynamic form questions.
 * No other values may be stored in t_section_question.questionType.
 *
 * @author Piramal Swasthya
 */
public enum QuestionType {
    /** Single-select from a predefined list of options. */
    RADIO,
    /** Multi-select from a predefined list of options. */
    MCQ,
    /** Free-text input. */
    TEXT,
    /** Date picker input. */
    DATE,
    /** Read-only display text — carries no answer data. */
    DISPLAY,
    /** Value auto-filled from context (e.g. ASHA worker ID). */
    AUTO_FILL
}
