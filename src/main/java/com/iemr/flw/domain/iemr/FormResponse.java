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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Root entity for a beneficiary's response to a dynamic form.
 * Tracks the exact form version used and the status lifecycle (DRAFT → SUBMITTED → COMPLETE).
 *
 * @author Piramal Swasthya
 */
@Entity
@Table(name = "t_form_response", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "responseId")
    private Long responseId;

    /** Identifies this response on every machine; generated when the row is created. Children link to it. */
    @Column(name = "responseUuid", length = 36, updatable = false)
    private String responseUuid;

    @Column(name = "beneficiaryId", nullable = false)
    private Long beneficiaryId;

    /**
     * The form, by UUID. The legacy numeric formId/versionId columns are no longer written: definition
     * IDs differ on every machine, so a response synced to central would point to the wrong form.
     */
    @Column(name = "formUuid", length = 100)
    private String formUuid;

    /** Version of the form this response was captured against (the latest at creation time). */
    @Column(name = "versionNumber")
    private Integer versionNumber;

    /** Logged-in ASHA/officer ID from the request body. */
    @Column(name = "officerId", nullable = false)
    private Long officerId;

    /** DRAFT | SUBMITTED | COMPLETE */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Set when PRE_SUBMIT sections are saved via /submit. */
    @Column(name = "submittedAt")
    private Timestamp submittedAt;

    /** Set when POST_SUBMIT sections are saved via /complete. */
    @Column(name = "completedAt")
    private Timestamp completedAt;

    /** Updated to now() each time the most-recently-completed section is saved (PRE_SUBMIT or any
     *  POST_SUBMIT section). The follow-up notification scheduler uses this as its reference date. */
    @Column(name = "last_follow_up_at")
    private Timestamp lastFollowUpAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updatedAt", nullable = false)
    private Timestamp updatedAt;

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
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
        if (responseUuid == null) {
            responseUuid = java.util.UUID.randomUUID().toString();
        }
        if (status == null) {
            status = "DRAFT";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
