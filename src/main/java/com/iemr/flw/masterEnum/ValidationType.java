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
 * Exhaustive set of validation rule types for dynamic form questions.
 * No other values may be stored in t_question_validation.validationType.
 *
 * @author Piramal Swasthya
 */
public enum ValidationType {
    /** Maximum character length for text inputs. validationParam = integer string e.g. "100". */
    MAX_LENGTH,
    /** Minimum allowed date. validationParam = ISO date string or "TODAY". */
    MIN_DATE,
    /** Maximum allowed date. validationParam = ISO date string or "TODAY". */
    MAX_DATE,
    /** Java-compatible regular expression the answer must match. validationParam = regex pattern. */
    REGEX,
    /** Field becomes mandatory when another field equals a specific value.
     *  validationParam format: "QUESTION_UUID=OPTION_VALUE" e.g. "Q-GENDER=FEMALE". */
    MANDATORY_IF
}