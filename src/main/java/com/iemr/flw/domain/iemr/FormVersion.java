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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A versioned snapshot of a dynamic form's section structure.
 * Every structural change creates a new FormVersion; only one version has isLatest=true per form.
 *
 * @author Piramal Swasthya
 */
@Entity
@Table(name = "t_form_version", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "versionId")
    private Long versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formId", nullable = false)
    @ToString.Exclude
    private DynamicForm dynamicForm;

    @Column(name = "versionNumber", nullable = false)
    private Integer versionNumber;

    @Column(name = "isLatest", nullable = false)
    private Boolean isLatest = true;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "createdBy", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "formVersion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<FormSection> sections = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}
