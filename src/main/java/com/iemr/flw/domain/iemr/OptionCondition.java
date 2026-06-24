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

import java.sql.Timestamp;

/**
 * Conditional action triggered when a specific option is selected.
 * actionType: SHOW_QUESTION | DISABLE_SECTION_VALIDATION | LOCK_FORM
 * Exactly one of targetQuestion / targetSection is non-null per row.
 */
@Entity
@Table(name = "t_option_condition", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conditionId")
    private Long conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionId", nullable = false)
    @ToString.Exclude
    private QuestionOption questionOption;

    @Column(name = "actionType", nullable = false, length = 40)
    private String actionType;

    /** Set when actionType = SHOW_QUESTION. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetQuestionId", nullable = true)
    @ToString.Exclude
    private SectionQuestion targetQuestion;

    /** Set when actionType = DISABLE_SECTION_VALIDATION. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetSectionId", nullable = true)
    @ToString.Exclude
    private FormSection targetSection;

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

    @Column(name = "SyncFailureReason", length = 255)
    private String syncFailureReason;
}
