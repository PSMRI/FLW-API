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

import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.OptionCondition;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.QuestionValidation;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.mapper.DynamicFormMapper;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.repo.iemr.FormSectionRepo;
import com.iemr.flw.repo.iemr.FormVersionRepo;
import com.iemr.flw.repo.iemr.OptionConditionRepo;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.QuestionValidationRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.service.DynamicFormDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of DynamicFormDefinitionService with version-aware writes.
 * Every structural change clones all sections fresh into a new FormVersion.
 * Sections have a direct FK to FormVersion (no join table).
 *
 * @author Piramal Swasthya
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DynamicFormDefinitionServiceImpl implements DynamicFormDefinitionService {

    private final DynamicFormRepo formRepo;
    private final FormSectionRepo sectionRepo;
    private final SectionQuestionRepo questionRepo;
    private final QuestionOptionRepo optionRepo;
    private final OptionConditionRepo conditionRepo;
    private final QuestionValidationRepo validationRepo;
    private final FormVersionRepo versionRepo;
    private final DynamicFormMapper mapper;

    // ── PUBLIC WRITE METHODS ──────────────────────────────────────────────────

    @Override
    @Transactional
    public DynamicFormDTO createForm(DynamicFormDTO dto) {
        formRepo.findByFormTypeAndIsActive(dto.getFormType(), true).ifPresent(existing ->  {
            throw new IllegalStateException(
                    "An active form of type " + dto.getFormType() + " already exists (formId="
                    + existing.getFormId() + "). Deactivate it before creating a new one.");
        });
        DynamicForm form = mapper.toEntity(dto);
        // TODO: set audit fields when auth is re-enabled for /dynamicForm endpoints
        // form.setCreatedBy(jwtUtil.extractUsername(authorization));
        // form.setUpdatedBy(jwtUtil.extractUsername(authorization));
        DynamicForm savedForm = formRepo.save(form);
        FormVersion version = createNewVersion(savedForm, 1, null, null);
        buildSectionsFromDto(dto.getSections(), version);
        log.info("Form created: {} (v1)", savedForm.getFormUuid());
        return loadLatestFromDb(savedForm.getFormId());
    }

    @Override
    @Transactional
    public DynamicFormDTO updateForm(Long formId, DynamicFormDTO dto) {
        DynamicForm form = requireForm(formId);
        FormVersion current = requireLatestVersion(formId);
        int nextVersion = current.getVersionNumber() + 1;
        FormVersion newVersion = createNewVersion(form, nextVersion, null, null);
        buildSectionsFromDto(dto.getSections(), newVersion);
        current.setIsLatest(false);
        versionRepo.save(current);
        log.info("Form updated: {} -> v{}", form.getFormUuid(), nextVersion);
        return loadLatestFromDb(formId);
    }

    @Override
    @Transactional
    public void activateForm(Long formId) {
        DynamicForm form = requireForm(formId);
        form.setIsActive(true);
        formRepo.save(form);
        log.info("Form activated: {}", form.getFormUuid());
    }

    @Override
    @Transactional
    public void deactivateForm(Long formId) {
        DynamicForm form = requireForm(formId);
        form.setIsActive(false);
        formRepo.save(form);
        log.info("Form deactivated: {}", form.getFormUuid());
    }

    @Override
    public DynamicFormDTO getFormDefinition(Long formId) {
        return loadLatestFromDb(formId);
    }

    @Override
    public DynamicFormDTO getFormDefinitionByVersion(Long formId, Integer versionNumber) {
        FormVersion version = versionRepo.findByDynamicForm_FormIdAndVersionNumber(formId, versionNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Version " + versionNumber + " not found for formId: " + formId));
        return buildFormDto(version.getDynamicForm(), version);
    }

    @Override
    public List<DynamicFormDTO> getAllForms() {
        return formRepo.findByIsActiveOrderByFormIdAsc(true)
                .stream()
                .map(form -> {
                    try {
                        return loadLatestFromDb(form.getFormId());
                    } catch (RuntimeException e) {
                        log.warn("Skipping form {} — no active version: {}", form.getFormId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ── VERSION MANAGEMENT ────────────────────────────────────────────────────

    private FormVersion createNewVersion(DynamicForm form, int versionNumber,
            String createdBy, String notes) {
        FormVersion version = new FormVersion();
        version.setDynamicForm(form);
        version.setVersionNumber(versionNumber);
        version.setIsLatest(true);
        version.setCreatedBy(createdBy);
        // TODO: set audit fields when auth is re-enabled for /dynamicForm endpoints
        // version.setCreatedBy(jwtUtil.extractUsername(authorization));
        // version.setUpdatedBy(jwtUtil.extractUsername(authorization));
        version.setNotes(notes);
        return versionRepo.save(version);
    }

    // ── BUILD HELPERS (DTO → Entity) ──────────────────────────────────────────

    /**
     * Two-pass build:
     * Pass 1 — persist sections, questions, options, validations; collect uuid→entity maps.
     * Pass 2 — wire conditions using targetQuestionUuid/targetSectionUuid from those maps.
     */
    private void buildSectionsFromDto(List<FormSectionDTO> dtos, FormVersion version) {
        if (dtos == null) return;

        Map<String, FormSection> sectionByUuid = new LinkedHashMap<>();
        Map<String, SectionQuestion> questionByUuid = new LinkedHashMap<>();
        Map<QuestionOption, List<OptionConditionDTO>> pendingConditions = new LinkedHashMap<>();

        // Pass 1: sections → questions → options + validations
        for (FormSectionDTO sDto : dtos) {
            FormSection section = mapper.toEntity(sDto);
            section.setFormVersion(version);
            section.setQuestions(new ArrayList<>());
            FormSection savedSection = sectionRepo.save(section);
            sectionByUuid.put(savedSection.getSectionUuid(), savedSection);

            if (sDto.getQuestions() == null) continue;
            for (SectionQuestionDTO qDto : sDto.getQuestions()) {
                SectionQuestion q = mapper.toEntity(qDto);
                q.setFormSection(savedSection);
                q.setOptions(new ArrayList<>());
                q.setValidations(new ArrayList<>());
                SectionQuestion savedQ = questionRepo.save(q);
                questionByUuid.put(savedQ.getQuestionUuid(), savedQ);

                if (qDto.getOptions() != null) {
                    for (QuestionOptionDTO oDto : qDto.getOptions()) {
                        QuestionOption opt = mapper.toEntity(oDto);
                        opt.setSectionQuestion(savedQ);
                        opt.setConditions(new ArrayList<>());
                        QuestionOption savedOpt = optionRepo.save(opt);
                        if (oDto.getConditions() != null && !oDto.getConditions().isEmpty()) {
                            pendingConditions.put(savedOpt, oDto.getConditions());
                        }
                    }
                }
                if (qDto.getValidations() != null) {
                    for (QuestionValidationDTO vDto : qDto.getValidations()) {
                        buildValidationEntity(vDto, savedQ);
                    }
                }
            }
        }

        // Pass 2: conditions — resolve targets by UUID (create path) or ID (addCondition path)
        for (Map.Entry<QuestionOption, List<OptionConditionDTO>> entry : pendingConditions.entrySet()) {
            QuestionOption opt = entry.getKey();
            for (OptionConditionDTO cDto : entry.getValue()) {
                OptionCondition cond = new OptionCondition();
                cond.setActionType(cDto.getActionType());
                cond.setQuestionOption(opt);

                if (cDto.getTargetQuestionUuid() != null) {
                    SectionQuestion target = questionByUuid.get(cDto.getTargetQuestionUuid());
                    if (target == null) throw new RuntimeException(
                            "targetQuestionUuid not found in this form: " + cDto.getTargetQuestionUuid());
                    cond.setTargetQuestion(target);
                } else if (cDto.getTargetQuestionId() != null) {
                    cond.setTargetQuestion(requireQuestion(cDto.getTargetQuestionId()));
                }

                if (cDto.getTargetSectionUuid() != null) {
                    FormSection target = sectionByUuid.get(cDto.getTargetSectionUuid());
                    if (target == null) throw new RuntimeException(
                            "targetSectionUuid not found in this form: " + cDto.getTargetSectionUuid());
                    cond.setTargetSection(target);
                } else if (cDto.getTargetSectionId() != null) {
                    cond.setTargetSection(requireSection(cDto.getTargetSectionId()));
                }

                conditionRepo.save(cond);
            }
        }
    }

    private QuestionValidation buildValidationEntity(QuestionValidationDTO dto,
            SectionQuestion question) {
        QuestionValidation v = mapper.toEntity(dto);
        v.setSectionQuestion(question);
        return validationRepo.save(v);
    }

    // ── LOAD / READ HELPERS ───────────────────────────────────────────────────

    private DynamicFormDTO loadLatestFromDb(Long formId) {
        FormVersion version = versionRepo.findByDynamicForm_FormIdAndIsLatest(formId, true)
                .orElseThrow(() -> new RuntimeException("No active version for formId: " + formId));
        return buildFormDto(version.getDynamicForm(), version);
    }

    /**
     * Builds the full form DTO using exactly 5 queries (constant regardless of form size).
     * All grouping, visibleByDefault computation, and embedded question/section population
     * happen in memory — zero extra queries beyond the initial 5.
     */
    private DynamicFormDTO buildFormDto(DynamicForm form, FormVersion version) {
        // Query 1: sections
        List<FormSection> sections = sectionRepo
                .findByFormVersion_VersionIdOrderByDisplayOrderAsc(version.getVersionId());
        if (sections.isEmpty()) {
            DynamicFormDTO dto = mapper.toDto(form);
            dto.setVersionNumber(version.getVersionNumber());
            dto.setSections(Collections.emptyList());
            return dto;
        }

        List<Long> sectionIds = sections.stream().map(FormSection::getSectionId)
                .collect(Collectors.toList());

        // Query 2: questions
        List<SectionQuestion> allQuestions = questionRepo
                .findBySectionIdsOrderByDisplayOrderAsc(sectionIds);

        List<Long> questionIds = allQuestions.stream().map(SectionQuestion::getQuestionId)
                .collect(Collectors.toList());

        List<QuestionOption> allOptions;
        List<QuestionValidation> allValidations;
        List<OptionCondition> allConditions;

        if (questionIds.isEmpty()) {
            allOptions = Collections.emptyList();
            allValidations = Collections.emptyList();
            allConditions = Collections.emptyList();
        } else {
            // Query 3: options
            allOptions = optionRepo.findByQuestionIdsOrderByDisplayOrderAsc(questionIds);
            // Query 4: validations
            allValidations = validationRepo.findByQuestionIds(questionIds);

            List<Long> optionIds = allOptions.stream().map(QuestionOption::getOptionId)
                    .collect(Collectors.toList());
            // Query 5: conditions (LEFT JOIN FETCH targets)
            allConditions = optionIds.isEmpty() ? Collections.emptyList()
                    : conditionRepo.findByOptionIds(optionIds);
        }

        // Compute hidden question IDs (those targeted by any condition)
        Set<Long> hiddenIds = allConditions.stream()
                .filter(c -> c.getTargetQuestion() != null)
                .map(c -> c.getTargetQuestion().getQuestionId())
                .collect(Collectors.toSet());

        // Build index maps — no extra queries; FKs already in memory via JOIN FETCH
        Map<Long, List<SectionQuestion>> questionsBySection = allQuestions.stream()
                .collect(Collectors.groupingBy(q -> q.getFormSection().getSectionId()));
        Map<Long, List<QuestionOption>> optionsByQuestion = allOptions.stream()
                .collect(Collectors.groupingBy(o -> o.getSectionQuestion().getQuestionId()));
        Map<Long, List<QuestionValidation>> validationsByQuestion = allValidations.stream()
                .collect(Collectors.groupingBy(v -> v.getSectionQuestion().getQuestionId()));
        Map<Long, List<OptionCondition>> conditionsByOption = allConditions.stream()
                .collect(Collectors.groupingBy(c -> c.getQuestionOption().getOptionId()));

        DynamicFormDTO dto = mapper.toDto(form);
        dto.setVersionNumber(version.getVersionNumber());
        dto.setSections(assembleSectionDtos(sections, questionsBySection, optionsByQuestion,
                validationsByQuestion, conditionsByOption, hiddenIds));
        return dto;
    }

    private List<FormSectionDTO> assembleSectionDtos(
            List<FormSection> sections,
            Map<Long, List<SectionQuestion>> questionsBySection,
            Map<Long, List<QuestionOption>> optionsByQuestion,
            Map<Long, List<QuestionValidation>> validationsByQuestion,
            Map<Long, List<OptionCondition>> conditionsByOption,
            Set<Long> hiddenIds) {
        return sections.stream().map(section -> {
            FormSectionDTO sDto = mapper.toDto(section);
            List<SectionQuestion> qs = questionsBySection
                    .getOrDefault(section.getSectionId(), Collections.emptyList());
            sDto.setQuestions(assembleQuestionDtos(qs, optionsByQuestion,
                    validationsByQuestion, conditionsByOption, hiddenIds));
            return sDto;
        }).collect(Collectors.toList());
    }

    private List<SectionQuestionDTO> assembleQuestionDtos(
            List<SectionQuestion> questions,
            Map<Long, List<QuestionOption>> optionsByQuestion,
            Map<Long, List<QuestionValidation>> validationsByQuestion,
            Map<Long, List<OptionCondition>> conditionsByOption,
            Set<Long> hiddenIds) {
        return questions.stream().map(q -> {
            SectionQuestionDTO qDto = mapper.toDto(q);
            qDto.setVisibleByDefault(!hiddenIds.contains(q.getQuestionId()));
            List<QuestionOption> opts = optionsByQuestion
                    .getOrDefault(q.getQuestionId(), Collections.emptyList());
            qDto.setOptions(assembleOptionDtos(opts, conditionsByOption));
            qDto.setValidations(validationsByQuestion
                    .getOrDefault(q.getQuestionId(), Collections.emptyList())
                    .stream().map(mapper::toDto).collect(Collectors.toList()));
            return qDto;
        }).collect(Collectors.toList());
    }

    private List<QuestionOptionDTO> assembleOptionDtos(
            List<QuestionOption> options,
            Map<Long, List<OptionCondition>> conditionsByOption) {
        return options.stream().map(opt -> {
            QuestionOptionDTO oDto = mapper.toDto(opt);
            oDto.setConditions(conditionsByOption
                    .getOrDefault(opt.getOptionId(), Collections.emptyList())
                    .stream().map(mapper::toDto).collect(Collectors.toList()));
            return oDto;
        }).collect(Collectors.toList());
    }

    // ── REQUIRE HELPERS ───────────────────────────────────────────────────────

    private DynamicForm requireForm(Long formId) {
        return formRepo.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
    }

    private FormVersion requireLatestVersion(Long formId) {
        return versionRepo.findByDynamicForm_FormIdAndIsLatest(formId, true)
                .orElseThrow(() -> new RuntimeException(
                        "No latest version found for formId: " + formId));
    }

    private FormSection requireSection(Long sectionId) {
        return sectionRepo.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found: " + sectionId));
    }

    private SectionQuestion requireQuestion(Long questionId) {
        return questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));
    }

}
