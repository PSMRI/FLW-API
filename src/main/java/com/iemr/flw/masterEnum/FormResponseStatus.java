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
 * Lifecycle status stored in FormResponse.status.
 *
 * @author Piramal Swasthya
 */
public enum FormResponseStatus {
    /** Saved via /submit. */
    SUBMITTED,
    /** Saved via /complete, consent question answered YES (or not applicable). */
    COMPLETE,
    /** Saved via /complete, consent question TB2_GI_Q1 answered NO. */
    REFUSED
}