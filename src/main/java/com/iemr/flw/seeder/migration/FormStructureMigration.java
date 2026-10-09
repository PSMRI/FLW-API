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

import com.iemr.flw.service.DynamicFormReconciliationService;

/**
 * One incremental, one-off structural change to a form that has already been created by one of the
 * {@code com.iemr.flw.seeder} classes — e.g. "add this new question to that existing section", or
 * "add this new option to that existing question". {@code DynamicFormMigrationRunner} discovers
 * every Spring bean implementing this interface, sorts them by {@link #migrationId()}, and applies
 * each exactly once, recording it in {@code t_dynamic_form_migration_log} so it is skipped on
 * every later restart. A migration should never be edited after it has shipped — write a new one
 * instead — since the log only records "has this id run", not what it actually did.
 *
 * Naming convention: {@code V<NNN>_ShortDescription}, e.g. {@code V001_AddSideEffectsQuestion},
 * zero-padded so lexicographic sort matches numeric order. Give each class a javadoc stating the
 * business reason for the change — that, plus the class itself, is the permanent changelog.
 *
 * Example:
 * <pre>{@code
 * @Component
 * public class V001_AddSideEffectsQuestionToTbCounselling implements FormStructureMigration {
 *
 *     @Override
 *     public String migrationId() {
 *         return "V001";
 *     }
 *
 *     @Override
 *     public void apply(DynamicFormReconciliationService svc) {
 *         FormVersion version = svc.latestVersion("TB_COUNSELLING")
 *                 .orElseThrow(() -> new IllegalStateException(
 *                         "TB_COUNSELLING not found — must run after TbCounsellingFormSeeder"));
 *         FormSection section = svc.section(version, "TB_SEC_B")
 *                 .orElseThrow(() -> new IllegalStateException("Section TB_SEC_B not found"));
 *
 *         SectionQuestionDTO question = new SectionQuestionDTO();
 *         question.setQuestionUuid("TB_B_Q7");
 *         question.setQuestionText("Side effects reported?");
 *         question.setQuestionType(QuestionType.RADIO);
 *         question.setOptions(List.of(yesOption, noOption));
 *         svc.ensureQuestion(section, question);
 *     }
 * }
 * }</pre>
 */
public interface FormStructureMigration {

    /** Stable id, e.g. "V001" — the migration-log primary key and the sort key for apply order. Never change it once shipped. */
    String migrationId();

    /**
     * Applies this migration's change(s) via the reconciliation service's ensureX methods.
     * Should remain safe to re-run (ensureX no-ops on rows that already exist) even though the
     * runner normally calls this only once per migrationId, per the tracking log.
     */
    void apply(DynamicFormReconciliationService reconciliationService);
}
