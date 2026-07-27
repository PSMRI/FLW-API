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

import com.iemr.flw.masterEnum.QuestionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A question within a form section.
 * questionType: RADIO | MCQ | CHECKBOX | TEXT | DATE | DISPLAY | AUTO_FILL
 */
@Entity
@Table(name = "t_section_question", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionId")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId", nullable = false)
    @ToString.Exclude
    private FormSection formSection;

    @Column(name = "questionUuid", nullable = false, length = 100)
    private String questionUuid;

    @Lob
    @Column(name = "questionText", nullable = false)
    private String questionText;

    @Column(name = "questionText_hindi", columnDefinition = "TEXT")
    private String questionTextHindi;

    @Enumerated(EnumType.STRING)
    @Column(name = "questionType", nullable = false, length = 20)
    private QuestionType questionType;

    @Column(name = "isMandatory", nullable = false)
    private Boolean isMandatory = true;

    @Column(name = "displayOrder", nullable = false)
    private Integer displayOrder;

    /** Convenience shortcut — enforced by FormValidationEngine for TEXT questions. */
    @Column(name = "maxLength")
    private Integer maxLength;

    /** e.g. "TODAY" for date fields with a default. */
    @Column(name = "defaultValue", length = 500)
    private String defaultValue;

    /** If true, answerText is encrypted via CryptoUtil before persistence. */
    @Column(name = "containsPii", nullable = false)
    private Boolean containsPii = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @OneToMany(mappedBy = "sectionQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<QuestionOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "sectionQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<QuestionValidation> validations = new ArrayList<>();

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
