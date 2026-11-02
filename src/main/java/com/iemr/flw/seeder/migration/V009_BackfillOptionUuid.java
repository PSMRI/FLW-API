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

import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Backfills t_question_option.optionUuid for every option of every form version created before
 * the column existed. The uuid is formType + optionValue, so the same option resolves to the same
 * optionUuid in v1, v2, … — letting clients map answers across versions, as with sectionUuid/questionUuid.
 * Only touches rows whose optionUuid is still blank, so re-running is a no-op.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class V009_BackfillOptionUuid implements FormStructureMigration {

    private final QuestionOptionRepo optionRepo;

    @Override
    public String migrationId() {
        return "V009";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        List<QuestionOption> options = optionRepo.findAllMissingOptionUuid();
        for (QuestionOption option : options) {
            option.setOptionUuid(QuestionOption.buildOptionUuid(
                    option.getSectionQuestion().getFormSection().getFormVersion().getDynamicForm().getFormType(),
                    option.getOptionValue()));
        }
        optionRepo.saveAll(options);
        log.info("V009: backfilled optionUuid on {} option(s).", options.size());
    }
}