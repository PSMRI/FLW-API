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

import com.iemr.flw.repo.iemr.DynamicFormMigrationLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Applies every {@link FormStructureMigration} bean, in {@link FormStructureMigration#migrationId()}
 * order, skipping ones already recorded in {@code t_dynamic_form_migration_log}.
 *
 * Runs as an {@link ApplicationRunner} rather than another {@code @PostConstruct} specifically so it
 * always runs after the 6 form seeders: Spring Boot invokes ApplicationRunners only once the whole
 * application context has finished refreshing, which is strictly after every bean's
 * {@code @PostConstruct} (the seeders included) has already completed — no {@code @Order}
 * coordination needed between the two mechanisms.
 *
 * A migration that throws propagates out of this loop and aborts startup — a broken migration
 * should fail loudly, not be silently skipped, and later migrations shouldn't apply out of order
 * behind a failed one.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicFormMigrationRunner implements ApplicationRunner {

    private final List<FormStructureMigration> migrations;
    private final DynamicFormMigrationLogRepo migrationLogRepo;
    private final DynamicFormMigrationApplier applier;

    @Override
    public void run(ApplicationArguments args) {
        migrations.stream()
                .sorted(Comparator.comparing(FormStructureMigration::migrationId))
                .forEach(this::applyIfNeeded);
    }

    private void applyIfNeeded(FormStructureMigration migration) {
        String id = migration.migrationId();
        if (migrationLogRepo.existsById(id)) {
            log.info("Form structure migration '{}' already applied — skipping.", id);
            return;
        }
        applier.apply(migration);
        log.info("Form structure migration '{}' applied.", id);
    }
}
