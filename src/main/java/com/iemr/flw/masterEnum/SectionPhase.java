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
 * Exhaustive set of phases a form section can belong to.
 * No other values may be stored in t_form_section.sectionPhase.
 *
 * @author Piramal Swasthya
 */
public enum SectionPhase {
    /** Shown first, before PRE_SUBMIT sections. */
    GENERAL_INFO,
    /** Shown before the form's main submit. Saved via /submit. */
    PRE_SUBMIT,
    /** Shown after the main submit. Saved via /complete. */
    POST_SUBMIT
}