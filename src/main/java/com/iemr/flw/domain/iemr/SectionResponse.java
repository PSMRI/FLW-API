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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Tracks completion state of one section within a form response.
 * sectionId FK to t_form_section already implies the form version via its version_id FK.
 *
 * @author Piramal Swasthya
 */
@Entity
@Table(name = "t_section_response", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sectionResponseId")
    private Long sectionResponseId;

    @Column(name = "responseId", nullable = false)
    private Long responseId;

    @Column(name = "sectionId", nullable = false)
    private Long sectionId;

    /** PENDING | IN_PROGRESS | DONE | SKIPPED */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "savedAt")
    private Timestamp savedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "vanID")
    private Integer vanID;

    @Column(name = "parkingPlaceID")
    private Integer parkingPlaceID;

    @Column(name = "processed")
    private String processed = "N";

    @Column(name = "vanSerialNo")
    private Long vanSerialNo;

    @Column(name = "SyncedDate")
    private Timestamp syncedDate;

    @Column(name = "Syncedby", length = 50)
    private String syncedBy;

    @Column(name = "SyncFailureReason")
    private String syncFailureReason;
}
