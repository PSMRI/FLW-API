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
    MANDATORY_IF,
    /** Display unit for a numeric answer. validationParam = unit label e.g. "sq. m.".
     *  Informational only — not a rule the answer is checked against. */
    UNIT,
    /** One English helper-text bullet shown under the question. validationParam = the bullet text.
     *  A question may have several; display them in validationId order. Informational only. */
    HELPER_TEXT_ENGLISH,
    /** Hindi counterpart of {@link #HELPER_TEXT_ENGLISH}, one bullet per row, in validationId order. */
    HELPER_TEXT_HINDI,
    /** The client must fill this answer itself instead of asking the user. validationParam = the source,
     *  e.g. "TODAY" or "CAMP_XRAY_RESULT". Informational only — not a rule the answer is checked against. */
    AUTO_FILL
}