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
package com.iemr.flw.seeder.migration;

import com.iemr.flw.domain.iemr.DynamicFormMigrationLog;
import com.iemr.flw.repo.iemr.DynamicFormMigrationLogRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies one {@link FormStructureMigration} and records it as applied, as a single atomic unit.
 * Kept as its own bean (rather than a method on {@link DynamicFormMigrationRunner}) so the
 * {@code @Transactional} proxy is actually honored — a runner calling this method on itself would
 * bypass Spring AOP's self-invocation limitation.
 */
@Service
@RequiredArgsConstructor
public class DynamicFormMigrationApplier {

    private final DynamicFormReconciliationService reconciliationService;
    private final DynamicFormMigrationLogRepo migrationLogRepo;

    @Transactional
    public void apply(FormStructureMigration migration) {
        migration.apply(reconciliationService);
        migrationLogRepo.save(new DynamicFormMigrationLog(migration.migrationId(), null));
    }
}
