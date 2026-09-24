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
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * In-place reconciliation for dynamic form structure — see {@link DynamicFormReconciliationService}.
 * Standalone: does not depend on {@link DynamicFormDefinitionServiceImpl}'s private helpers, since
 * that service's write path (full version clone) is a deliberately different mechanism from this one.
 *
 * @author Piramal Swasthya
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DynamicFormReconciliationServiceImpl implements DynamicFormReconciliationService {

    private final DynamicFormRepo formRepo;
    private final FormVersionRepo versionRepo;
    private final FormSectionRepo sectionRepo;
    private final SectionQuestionRepo questionRepo;
    private final QuestionOptionRepo optionRepo;
    private final QuestionValidationRepo validationRepo;
    private final OptionConditionRepo conditionRepo;
    private final DynamicFormMapper mapper;

    // ── Form / Version ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DynamicForm ensureForm(DynamicFormDTO dto) {
        Optional<DynamicForm> existing = formRepo.findByFormUuid(dto.getFormUuid());
        if (existing.isPresent()) {
            DynamicForm form = existing.get();
            if (Boolean.FALSE.equals(form.getIsActive())) {
                form.setIsActive(true);
                form = formRepo.save(form);
                log.info("Form '{}' was inactive — reactivated.", dto.getFormUuid());
            }
            return form;
        }
        DynamicForm form = mapper.toEntity(dto);
        form.setFormId(null);
        return formRepo.save(form);
    }

    @Override
    @Transactional
    public FormVersion ensureLatestVersion(DynamicForm form) {
        return versionRepo.findByDynamicForm_FormIdAndIsLatest(form.getFormId(), true)
                .orElseGet(() -> {
                    FormVersion version = new FormVersion();
                    version.setDynamicForm(form);
                    version.setVersionNumber(1);
                    version.setIsLatest(true);
                    return versionRepo.save(version);
                });
    }

    // ── Section / Question / Option / Validation / Condition ─────────────────

    @Override
    @Transactional
    public FormSection ensureSection(FormVersion version, FormSectionDTO dto) {
        return sectionRepo.findByFormVersion_VersionIdAndSectionUuid(version.getVersionId(), dto.getSectionUuid())
                .orElseGet(() -> createSection(version, dto));
    }

    @Override
    @Transactional
    public SectionQuestion ensureQuestion(FormSection section, SectionQuestionDTO dto) {
        return questionRepo.findByFormSection_SectionIdAndQuestionUuid(section.getSectionId(), dto.getQuestionUuid())
                .orElseGet(() -> createQuestion(section, dto));
    }

    @Override
    @Transactional
    public QuestionOption ensureOption(SectionQuestion question, QuestionOptionDTO dto) {
        return optionRepo.findBySectionQuestion_QuestionIdAndOptionValue(question.getQuestionId(), dto.getOptionValue())
                .orElseGet(() -> createOption(question, dto));
    }

    @Override
    @Transactional
    public QuestionValidation ensureValidation(SectionQuestion question, QuestionValidationDTO dto) {
        return validationRepo.findBySectionQuestion_QuestionIdAndValidationTypeAndValidationParam(
                        question.getQuestionId(), dto.getValidationType(), dto.getValidationParam())
                .orElseGet(() -> createValidation(question, dto));
    }

    @Override
    @Transactional
    public OptionCondition ensureCondition(QuestionOption option, OptionConditionDTO dto, FormVersion version) {
        Optional<OptionCondition> existing;
        if (dto.getTargetQuestionUuid() != null) {
            existing = conditionRepo.findByQuestionOption_OptionIdAndActionTypeAndTargetQuestion_QuestionUuid(
                    option.getOptionId(), dto.getActionType(), dto.getTargetQuestionUuid());
        } else if (dto.getTargetSectionUuid() != null) {
            existing = conditionRepo.findByQuestionOption_OptionIdAndActionTypeAndTargetSection_SectionUuid(
                    option.getOptionId(), dto.getActionType(), dto.getTargetSectionUuid());
        } else {
            existing = Optional.empty();
        }
        return existing.orElseGet(() -> createCondition(option, dto, version));
    }

    // ── Update / Remove ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SectionQuestion updateQuestion(Long questionId, SectionQuestionDTO dto) {
        SectionQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found: " + questionId));
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionTextHindi(dto.getQuestionTextHindi());
        question.setQuestionType(dto.getQuestionType());
        question.setIsMandatory(dto.getIsMandatory());
        question.setMaxLength(dto.getMaxLength());
        question.setDefaultValue(dto.getDefaultValue());
        question.setContainsPii(dto.getContainsPii());
        if (dto.getDisplayOrder() != null && !dto.getDisplayOrder().equals(question.getDisplayOrder())) {
            moveQuestion(question, dto.getDisplayOrder());
        }
        return questionRepo.save(question);
    }

    @Override
    @Transactional
    public void unlinkQuestion(Long questionId) {
        SectionQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found: " + questionId));
        question.setIsActive(false);
        questionRepo.save(question);
    }

    @Override
    @Transactional
    public void removeOption(Long optionId) {
        QuestionOption option = optionRepo.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("Option not found: " + optionId));
        option.setIsActive(false);
        optionRepo.save(option);
    }

    // ── Read-only lookups ──────────────────────────────────────────────────────

    @Override
    public Optional<FormVersion> latestVersion(String formUuid) {
        return formRepo.findByFormUuid(formUuid)
                .flatMap(form -> versionRepo.findByDynamicForm_FormIdAndIsLatest(form.getFormId(), true));
    }

    @Override
    public Optional<FormSection> section(FormVersion version, String sectionUuid) {
        return sectionRepo.findByFormVersion_VersionIdAndSectionUuid(version.getVersionId(), sectionUuid);
    }

    @Override
    public Optional<SectionQuestion> question(FormSection section, String questionUuid) {
        return questionRepo.findByFormSection_SectionIdAndQuestionUuid(section.getSectionId(), questionUuid);
    }

    @Override
    public Optional<QuestionOption> option(SectionQuestion question, String optionValue) {
        return optionRepo.findBySectionQuestion_QuestionIdAndOptionValue(question.getQuestionId(), optionValue);
    }

    // ── Create helpers ─────────────────────────────────────────────────────────

    private FormSection createSection(FormVersion version, FormSectionDTO dto) {
        int order = resolveOrder(dto.getDisplayOrder(),
                () -> sectionRepo.findTopByFormVersion_VersionIdOrderByDisplayOrderDesc(version.getVersionId())
                        .map(FormSection::getDisplayOrder),
                from -> sectionRepo.shiftDisplayOrder(version.getVersionId(), from, Integer.MAX_VALUE, 1));
        FormSection section = mapper.toEntity(dto);
        section.setSectionId(null);
        section.setFormVersion(version);
        section.setDisplayOrder(order);
        section.setQuestions(new ArrayList<>());
        return sectionRepo.save(section);
    }

    private SectionQuestion createQuestion(FormSection section, SectionQuestionDTO dto) {
        int order = resolveOrder(dto.getDisplayOrder(),
                () -> questionRepo.findTopByFormSection_SectionIdOrderByDisplayOrderDesc(section.getSectionId())
                        .map(SectionQuestion::getDisplayOrder),
                from -> questionRepo.shiftDisplayOrder(section.getSectionId(), from, Integer.MAX_VALUE, 1));
        SectionQuestion question = mapper.toEntity(dto);
        question.setQuestionId(null);
        question.setFormSection(section);
        question.setDisplayOrder(order);
        question.setOptions(new ArrayList<>());
        question.setValidations(new ArrayList<>());
        return questionRepo.save(question);
    }

    private QuestionOption createOption(SectionQuestion question, QuestionOptionDTO dto) {
        int order = resolveOrder(dto.getDisplayOrder(),
                () -> optionRepo.findTopBySectionQuestion_QuestionIdOrderByDisplayOrderDesc(question.getQuestionId())
                        .map(QuestionOption::getDisplayOrder),
                from -> optionRepo.shiftDisplayOrder(question.getQuestionId(), from, Integer.MAX_VALUE, 1));
        QuestionOption option = mapper.toEntity(dto);
        option.setOptionId(null);
        option.setSectionQuestion(question);
        option.setDisplayOrder(order);
        option.setConditions(new ArrayList<>());
        return optionRepo.save(option);
    }

    private QuestionValidation createValidation(SectionQuestion question, QuestionValidationDTO dto) {
        QuestionValidation validation = mapper.toEntity(dto);
        validation.setValidationId(null);
        validation.setSectionQuestion(question);
        return validationRepo.save(validation);
    }

    private OptionCondition createCondition(QuestionOption option, OptionConditionDTO dto, FormVersion version) {
        OptionCondition condition = mapper.toEntity(dto);
        condition.setConditionId(null);
        condition.setQuestionOption(option);

        if (dto.getTargetQuestionUuid() != null) {
            condition.setTargetQuestion(questionRepo
                    .findByFormSection_FormVersion_VersionIdAndQuestionUuid(version.getVersionId(), dto.getTargetQuestionUuid())
                    .orElseThrow(() -> new NoSuchElementException(
                            "targetQuestionUuid not found in this form version: " + dto.getTargetQuestionUuid())));
        } else if (dto.getTargetQuestionId() != null) {
            condition.setTargetQuestion(questionRepo.findById(dto.getTargetQuestionId())
                    .orElseThrow(() -> new NoSuchElementException("Question not found: " + dto.getTargetQuestionId())));
        }

        if (dto.getTargetSectionUuid() != null) {
            condition.setTargetSection(sectionRepo
                    .findByFormVersion_VersionIdAndSectionUuid(version.getVersionId(), dto.getTargetSectionUuid())
                    .orElseThrow(() -> new NoSuchElementException(
                            "targetSectionUuid not found in this form version: " + dto.getTargetSectionUuid())));
        } else if (dto.getTargetSectionId() != null) {
            condition.setTargetSection(sectionRepo.findById(dto.getTargetSectionId())
                    .orElseThrow(() -> new NoSuchElementException("Section not found: " + dto.getTargetSectionId())));
        }

        return conditionRepo.save(condition);
    }

    /**
     * Omitted displayOrder → append after the current max sibling (no shift needed).
     * Explicit displayOrder → push every sibling at or after that position up by one, then use it as-is.
     */
    private int resolveOrder(Integer requested, Supplier<Optional<Integer>> currentMaxSupplier,
            IntConsumer shiftFromInclusive) {
        if (requested == null) {
            return currentMaxSupplier.get().orElse(0) + 1;
        }
        shiftFromInclusive.accept(requested);
        return requested;
    }

    /**
     * Moves a question already at oldPos to newPos within its section, shifting the siblings in
     * between to keep displayOrder contiguous — same three-branch logic a full reorder needs
     * regardless of direction.
     */
    private void moveQuestion(SectionQuestion question, int newPos) {
        int oldPos = question.getDisplayOrder();
        Long sectionId = question.getFormSection().getSectionId();
        if (newPos < oldPos) {
            questionRepo.shiftDisplayOrder(sectionId, newPos, oldPos - 1, 1);
        } else {
            questionRepo.shiftDisplayOrder(sectionId, oldPos + 1, newPos, -1);
        }
        question.setDisplayOrder(newPos);
    }
}
