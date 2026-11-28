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
package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.repo.iemr.FormSectionRepo;
import com.iemr.flw.repo.iemr.FormVersionRepo;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Finds the definition rows a stored response points to. Response rows reference the definition by
 * UUID (formUuid + versionNumber, sectionUuid, questionUuid, optionUuid), never by this machine's numeric
 * IDs. sectionUuid and questionUuid repeat in every version and optionUuid is shared between questions,
 * so every lookup is scoped: version, then section / question within it, then option within the question.
 * Inactive definition rows are included so historical answers still resolve.
 *
 * @author Piramal Swasthya
 */
@RequiredArgsConstructor
@Component
public class ResponseDefinitionResolver {

    private final FormVersionRepo formVersionRepo;
    private final FormSectionRepo formSectionRepo;
    private final SectionQuestionRepo sectionQuestionRepo;
    private final QuestionOptionRepo questionOptionRepo;

    /**
     * The definition of {@code formUuid}'s {@code versionNumber}, loaded once per cache, or null when that
     * version is not on this machine. Pass a new map per request; it is not thread-safe.
     */
    public VersionDefinition load(String formUuid, Integer versionNumber, Map<String, VersionDefinition> cache) {
        if (formUuid == null || versionNumber == null) {
            return null;
        }
        String key = formUuid + "#" + versionNumber;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        VersionDefinition definition = formVersionRepo
                .findFirstByDynamicForm_FormUuidAndVersionNumberOrderByVersionIdDesc(formUuid, versionNumber)
                .map(this::loadVersion)
                .orElse(null);
        cache.put(key, definition);
        return definition;
    }

    private VersionDefinition loadVersion(FormVersion version) {
        Long versionId = version.getVersionId();

        Map<String, FormSection> sectionByUuid = new HashMap<>();
        for (FormSection section : formSectionRepo.findByFormVersion_VersionIdOrderByDisplayOrderAsc(versionId)) {
            sectionByUuid.putIfAbsent(section.getSectionUuid(), section);
        }
        Map<String, SectionQuestion> questionByUuid = new HashMap<>();
        for (SectionQuestion question : sectionQuestionRepo.findAllByVersionIdIncludingInactive(versionId)) {
            questionByUuid.putIfAbsent(question.getQuestionUuid(), question);
        }
        Map<String, Map<String, QuestionOption>> optionsByQuestionUuid = new HashMap<>();
        for (QuestionOption option : questionOptionRepo.findAllByVersionIdIncludingInactive(versionId)) {
            if (option.getOptionUuid() != null) {
                optionsByQuestionUuid
                        .computeIfAbsent(option.getSectionQuestion().getQuestionUuid(), k -> new HashMap<>())
                        .putIfAbsent(option.getOptionUuid(), option);
            }
        }
        return new VersionDefinition(version, sectionByUuid, questionByUuid, optionsByQuestionUuid);
    }

    /** One form version's sections, questions and options, keyed by UUID. */
    public record VersionDefinition(
            FormVersion version,
            Map<String, FormSection> sectionByUuid,
            Map<String, SectionQuestion> questionByUuid,
            Map<String, Map<String, QuestionOption>> optionsByQuestionUuid) {

        public FormSection section(String sectionUuid) {
            return sectionUuid == null ? null : sectionByUuid.get(sectionUuid);
        }

        public SectionQuestion question(String questionUuid) {
            return questionUuid == null ? null : questionByUuid.get(questionUuid);
        }

        public QuestionOption option(String questionUuid, String optionUuid) {
            if (questionUuid == null || optionUuid == null) {
                return null;
            }
            return optionsByQuestionUuid.getOrDefault(questionUuid, Map.of()).get(optionUuid);
        }
    }
}
