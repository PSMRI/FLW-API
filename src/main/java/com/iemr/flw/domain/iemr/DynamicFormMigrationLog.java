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
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * One row per applied {@code com.iemr.flw.seeder.migration.FormStructureMigration}, keyed by its
 * stable migrationId — lets {@code DynamicFormMigrationRunner} skip a migration it has already run.
 */
@Entity
@Table(name = "t_dynamic_form_migration_log", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormMigrationLog {

    @Id
    @Column(name = "migrationId", length = 50)
    private String migrationId;

    @CreationTimestamp
    @Column(name = "appliedAt", nullable = false, updatable = false)
    private Timestamp appliedAt;
}
