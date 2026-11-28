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
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Stores one answer to a single question within a section response.
 * MCQ/CHECKBOX_MULTI/DROPDOWN_MULTI questions produce multiple rows (one per selected option) — no unique constraint on (sectionResponseId, questionId).
 * The question and option are referenced by UUID (questionUuid within the response's form version,
 * optionUuid within the question); the legacy numeric questionId/optionId columns are no longer written.
 * RADIO/DROPDOWN: optionUuid set, answerText null.
 * MCQ/CHECKBOX_MULTI/DROPDOWN_MULTI: optionUuid set per selection, answerText null.
 * TEXT/DATE/AUTO_FILL/NUMBER_PICKER: answerText set, optionUuid null.
 * DISPLAY: no row stored (read-only question type).
 *
 * @author Piramal Swasthya
 */
@Entity
@Table(name = "t_question_response", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionResponseId")
    private Long questionResponseId;

    /** Identifies this row on every machine; generated when the row is created. */
    @Column(name = "questionResponseUuid", length = 36, updatable = false)
    private String questionResponseUuid;

    /** This machine's parent ID, kept for local use (change-log triggers). Not uploaded: NULL on central rows. */
    @Column(name = "sectionResponseId")
    private Long sectionResponseId;

    /** The parent link that works on every machine; reads and deletes go through it. */
    @Column(name = "sectionResponseUuid", length = 36)
    private String sectionResponseUuid;

    @Column(name = "questionUuid", length = 100)
    private String questionUuid;

    /** Set for RADIO/DROPDOWN (one row) and MCQ/CHECKBOX_MULTI/DROPDOWN_MULTI (one row per selected option). */
    @Column(name = "optionUuid", length = 255)
    private String optionUuid;

    /** Set for TEXT, DATE, AUTO_FILL answers. */
    @Lob
    @Column(name = "answerText")
    private String answerText;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "vanID")
    private Integer vanID;

    @Column(name = "parkingPlaceID")
    private Integer parkingPlaceID;

    @Column(name = "processed")
    @Builder.Default
    private String processed = "N";

    @Column(name = "vanSerialNo")
    private Long vanSerialNo;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "SyncedDate")
    private Timestamp syncedDate;

    @Column(name = "Syncedby", length = 50)
    private String syncedBy;

    @Column(name = "SyncFailureReason")
    private String syncFailureReason;

    @PrePersist
    protected void onCreate() {
        if (questionResponseUuid == null) {
            questionResponseUuid = java.util.UUID.randomUUID().toString();
        }
    }
}
