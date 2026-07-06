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

import com.iemr.flw.domain.iemr.FormResponse;
import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.QuestionResponse;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.domain.iemr.SectionResponse;
import com.iemr.flw.dto.iemr.FormResponseDTO;
import com.iemr.flw.dto.iemr.FormResponseRequest;
import com.iemr.flw.dto.iemr.QuestionAnswerRequest;
import com.iemr.flw.dto.iemr.QuestionResponseDTO;
import com.iemr.flw.dto.iemr.SectionAnswerRequest;
import com.iemr.flw.dto.iemr.SectionResponseDTO;
import com.iemr.flw.repo.iemr.FormResponseRepo;
import com.iemr.flw.repo.iemr.FormSectionRepo;
import com.iemr.flw.repo.iemr.FormVersionRepo;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.QuestionResponseRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.repo.iemr.SectionResponseRepo;
import com.iemr.flw.masterEnum.FormResponseStatus;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.SectionPhase;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Saves a single dynamic form response in its own REQUIRES_NEW transaction.
 * Used by submitBulk so that each item in the batch is independently committed or rolled back —
 * a failed item does not affect sibling items in the same bulk request.
 *
 * @author Piramal Swasthya
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FormResponseItemSaver {

    private static final String SECTION_STATUS_DONE = "DONE";

    private final FormResponseRepo formResponseRepo;
    private final SectionResponseRepo sectionResponseRepo;
    private final QuestionResponseRepo questionResponseRepo;
    private final FormVersionRepo formVersionRepo;
    private final FormSectionRepo formSectionRepo;
    private final SectionQuestionRepo sectionQuestionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final JwtUtil jwtUtil;

    /**
     * Saves one form response in its own independent REQUIRES_NEW transaction.
     * Order: validate → delete old → save new. If save fails the transaction rolls back,
     * restoring the deleted rows automatically.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FormResponseDTO saveForBulk(FormResponseRequest req, String jwtToken) throws IEMRException {
        String actor = jwtUtil.extractUserId(jwtToken).toString();
        FormVersion version = resolveLatestVersion(req.getFormUuid());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Long formId = version.getDynamicForm().getFormId();

        // Step 1: Validate all section + question UUIDs BEFORE any writes or deletes
        validateRequest(req, version);

        // Step 2: Check for an existing response for this beneficiary+form
        List<FormResponse> existing =
                formResponseRepo.findByBeneficiaryIdAndFormId(req.getBeneficiaryId(), formId);

        FormResponse formResponse;
        if (!existing.isEmpty()) {
            formResponse = existing.get(0);
            Long responseId = formResponse.getResponseId();

            // Step 3: Update FormResponse header
            formResponse.setStatus(FormResponseStatus.SUBMITTED.name());
            formResponse.setSubmittedAt(now);
            formResponse.setUpdatedBy(actor);
            formResponse.setLastFollowUpAt(now);
            formResponse = formResponseRepo.save(formResponse);
            log.info("saveForBulk: overwriting responseId={} for beneficiaryId={}",
                    formResponse.getResponseId(), req.getBeneficiaryId());
        } else {
            formResponse = createFormResponse(req, version, now, actor);
        }

        // Step 5: Insert fresh sections and answers
        return processSections(formResponse, req, version, actor);
    }

    /** Pre-flight: validates every sectionUuid and questionUuid in the request exist in the form. */
    private void validateRequest(FormResponseRequest req, FormVersion version) {
        List<FormSection> allSections =
                formSectionRepo.findByFormVersion_VersionIdOrderByDisplayOrderAsc(version.getVersionId());
        Map<String, FormSection> sectionByUuid = allSections.stream()
                .collect(Collectors.toMap(FormSection::getSectionUuid, Function.identity()));

        Set<Long> sectionIds = allSections.stream()
                .map(FormSection::getSectionId)
                .collect(Collectors.toSet());
        List<SectionQuestion> allQuestions =
                sectionQuestionRepo.findBySectionIdsOrderByDisplayOrderAsc(sectionIds);
        Map<Long, Set<String>> questionUuidsBySectionId = allQuestions.stream()
                .collect(Collectors.groupingBy(
                        q -> q.getFormSection().getSectionId(),
                        Collectors.mapping(SectionQuestion::getQuestionUuid, Collectors.toSet())));

        for (SectionAnswerRequest sectionReq : req.getSections()) {
            FormSection section = sectionByUuid.get(sectionReq.getSectionUuid());
            if (section == null) {
                throw new NoSuchElementException(
                        "Section '" + sectionReq.getSectionUuid() +
                        "' not found in form '" + req.getFormUuid() + "'");
            }
            Set<String> validQuestions =
                    questionUuidsBySectionId.getOrDefault(section.getSectionId(), Set.of());
            for (QuestionAnswerRequest answer : sectionReq.getAnswers()) {
                if (answer.getQuestionUuid() != null
                        && !validQuestions.contains(answer.getQuestionUuid())) {
                    throw new NoSuchElementException(
                            "Question '" + answer.getQuestionUuid() +
                            "' not found in section '" + sectionReq.getSectionUuid() + "'");
                }
            }
        }
    }

    // ---- private helpers ----

    private FormVersion resolveLatestVersion(String formUuid) {
        return formVersionRepo.findByDynamicForm_FormUuidAndIsLatest(formUuid, true)
                .orElseThrow(() -> new NoSuchElementException(
                        "No active FormVersion found for form: " + formUuid));
    }

    private FormResponse createFormResponse(FormResponseRequest req, FormVersion version, Timestamp now, String actor) {
        FormResponse formResponse = FormResponse.builder()
                .beneficiaryId(req.getBeneficiaryId())
                .formId(version.getDynamicForm().getFormId())
                .versionId(version.getVersionId())
                .officerId(req.getOfficerId())
                .status(FormResponseStatus.SUBMITTED.name())
                .createdBy(actor)
                .updatedBy(actor)
                .submittedAt(now)
                .lastFollowUpAt(now)
                .build();
        return formResponseRepo.save(formResponse);
    }

    private FormResponseDTO processSections(FormResponse formResponse, FormResponseRequest req, FormVersion version, String actor) {
        List<FormSection> allSections =
                formSectionRepo.findByFormVersion_VersionIdOrderByDisplayOrderAsc(version.getVersionId());
        Map<String, FormSection> sectionByUuid = allSections.stream()
                .collect(Collectors.toMap(FormSection::getSectionUuid, Function.identity()));

        Set<Long> sectionIds = allSections.stream()
                .map(FormSection::getSectionId)
                .collect(Collectors.toSet());

        List<SectionQuestion> allQuestions =
                sectionQuestionRepo.findBySectionIdsOrderByDisplayOrderAsc(sectionIds);
        Map<Long, Map<String, SectionQuestion>> questionsBySection = allQuestions.stream()
                .collect(Collectors.groupingBy(
                        q -> q.getFormSection().getSectionId(),
                        Collectors.toMap(SectionQuestion::getQuestionUuid, Function.identity())));

        Set<Long> questionIds = allQuestions.stream()
                .map(SectionQuestion::getQuestionId)
                .collect(Collectors.toSet());
        List<QuestionOption> allOptions =
                questionOptionRepo.findByQuestionIdsOrderByDisplayOrderAsc(questionIds);
        Map<Long, Map<String, QuestionOption>> optionsByQuestion = allOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getSectionQuestion().getQuestionId(),
                        Collectors.toMap(QuestionOption::getOptionValue, Function.identity())));

        List<SectionResponseDTO> sectionDTOs = new ArrayList<>();
        for (SectionAnswerRequest sectionReq : req.getSections()) {
            if (sectionReq.getSectionUuid() == null) {
                throw new IllegalArgumentException(
                        "sectionUuid is null — did you send sectionCode instead?");
            }
            FormSection section = sectionByUuid.get(sectionReq.getSectionUuid());
            if (section == null) {
                throw new NoSuchElementException(
                        "Section '" + sectionReq.getSectionUuid() +
                        "' not found in form '" + req.getFormUuid() + "'");
            }

            Optional<SectionResponse> existingSr = sectionResponseRepo.findByResponseIdAndSectionId(
                    formResponse.getResponseId(), section.getSectionId());
            if (existingSr.isPresent() && Boolean.FALSE.equals(section.getIsEditable())) {
                log.info("saveForBulk: section '{}' is not editable — skipping update for responseId={}",
                        section.getSectionUuid(), formResponse.getResponseId());
                continue;
            }

            // Delete existing SectionResponse (and its QuestionResponses) for this section only
            existingSr.ifPresent(sr -> {
                questionResponseRepo.deleteBySectionResponseIdIn(Set.of(sr.getSectionResponseId()));
                sectionResponseRepo.deleteById(sr.getSectionResponseId());
            });

            SectionResponse sectionResponse = upsertSectionResponse(
                    formResponse.getResponseId(), section.getSectionId(), actor);

            if (section.getSectionPhase() == SectionPhase.POST_SUBMIT) {
                formResponse.setLastFollowUpAt(sectionResponse.getSavedAt());
                formResponseRepo.save(formResponse);
            }

            Map<String, SectionQuestion> questionMap =
                    questionsBySection.getOrDefault(section.getSectionId(), Map.of());

            List<QuestionResponse> questionResponses = processAnswers(
                    sectionResponse.getSectionResponseId(),
                    sectionReq.getAnswers(),
                    questionMap,
                    optionsByQuestion,
                    actor);

            questionResponseRepo.saveAll(questionResponses);
            sectionDTOs.add(buildSectionResponseDTO(sectionResponse, section, questionResponses));
        }

        return buildFormResponseDTO(formResponse, sectionDTOs);
    }

    private SectionResponse upsertSectionResponse(Long responseId, Long sectionId, String actor) {
        Optional<SectionResponse> existing =
                sectionResponseRepo.findByResponseIdAndSectionId(responseId, sectionId);
        if (existing.isPresent()) {
            SectionResponse sr = existing.get();
            sr.setStatus(SECTION_STATUS_DONE);
            sr.setSavedAt(new Timestamp(System.currentTimeMillis()));
            sr.setUpdatedBy(actor);
            return sectionResponseRepo.save(sr);
        }
        SectionResponse sr = SectionResponse.builder()
                .responseId(responseId)
                .sectionId(sectionId)
                .status(SECTION_STATUS_DONE)
                .savedAt(new Timestamp(System.currentTimeMillis()))
                .createdBy(actor)
                .updatedBy(actor)
                .build();
        return sectionResponseRepo.save(sr);
    }

    private List<QuestionResponse> processAnswers(
            Long sectionResponseId,
            List<QuestionAnswerRequest> answers,
            Map<String, SectionQuestion> questionMap,
            Map<Long, Map<String, QuestionOption>> optionsByQuestion,
            String actor) {

        List<QuestionResponse> results = new ArrayList<>();
        for (QuestionAnswerRequest answer : answers) {
            if (answer.getQuestionUuid() == null) {
                throw new IllegalArgumentException(
                        "questionUuid is null — did you send questionCode instead?");
            }
            SectionQuestion question = questionMap.get(answer.getQuestionUuid());
            if (question == null) {
                throw new NoSuchElementException(
                        "Question '" + answer.getQuestionUuid() + "' not found in section");
            }
            QuestionType type = question.getQuestionType();
            Long questionId = question.getQuestionId();

            if (type == QuestionType.DISPLAY) {
                continue;
            }

            questionResponseRepo.deleteByQuestionIdAndSectionResponseId(questionId, sectionResponseId);

            if (type == QuestionType.RADIO) {
                if (answer.getOptionValue() != null) {
                    QuestionOption opt = resolveOption(optionsByQuestion, questionId,
                            answer.getOptionValue(), answer.getQuestionUuid());
                    results.add(QuestionResponse.builder()
                            .sectionResponseId(sectionResponseId)
                            .questionId(questionId)
                            .optionId(opt != null ? opt.getOptionId() : null)
                            .createdBy(actor)
                            .updatedBy(actor)
                            .build());
                }
            } else if (type == QuestionType.MCQ) {
                if (answer.getOptionValues() != null) {
                    for (String val : answer.getOptionValues()) {
                        QuestionOption opt = resolveOption(optionsByQuestion, questionId, val,
                                answer.getQuestionUuid());
                        results.add(QuestionResponse.builder()
                                .sectionResponseId(sectionResponseId)
                                .questionId(questionId)
                                .optionId(opt != null ? opt.getOptionId() : null)
                                .createdBy(actor)
                                .updatedBy(actor)
                                .build());
                    }
                }
            } else {
                // TEXT, DATE, AUTO_FILL, CHECKBOX — prefer answerText, then answerDate, then optionValue (legacy)
                String value = answer.getAnswerText() != null ? answer.getAnswerText()
                        : answer.getAnswerDate() != null ? answer.getAnswerDate()
                        : answer.getOptionValue();
                results.add(QuestionResponse.builder()
                        .sectionResponseId(sectionResponseId)
                        .questionId(questionId)
                        .answerText(value)
                        .createdBy(actor)
                        .updatedBy(actor)
                        .build());
            }
        }
        return results;
    }

    private QuestionOption resolveOption(Map<Long, Map<String, QuestionOption>> optionsByQuestion,
            Long questionId, String optionValue, String questionUuid) {
        Map<String, QuestionOption> opts = optionsByQuestion.get(questionId);
        if (opts == null || !opts.containsKey(optionValue)) {
            log.warn("Option value '{}' not found for question '{}', storing null optionId",
                    optionValue, questionUuid);
            return null;
        }
        return opts.get(optionValue);
    }

    private FormResponseDTO buildFormResponseDTO(FormResponse r, List<SectionResponseDTO> sections) {
        return FormResponseDTO.builder()
                .responseId(r.getResponseId())
                .beneficiaryId(r.getBeneficiaryId())
                .formId(r.getFormId())
                .versionId(r.getVersionId())
                .officerId(r.getOfficerId())
                .status(r.getStatus())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .submittedAt(r.getSubmittedAt())
                .completedAt(r.getCompletedAt())
                .lastFollowUpAt(r.getLastFollowUpAt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .sections(sections)
                .build();
    }

    private SectionResponseDTO buildSectionResponseDTO(
            SectionResponse sr, FormSection section, List<QuestionResponse> answers) {
        List<QuestionResponseDTO> answerDTOs = answers.stream()
                .map(a -> QuestionResponseDTO.builder()
                        .questionResponseId(a.getQuestionResponseId())
                        .questionId(a.getQuestionId())
                        .optionId(a.getOptionId())
                        .answerText(a.getAnswerText())
                        .build())
                .collect(Collectors.toList());
        return SectionResponseDTO.builder()
                .sectionResponseId(sr.getSectionResponseId())
                .sectionId(sr.getSectionId())
                .sectionUuid(section != null ? section.getSectionUuid() : null)
                .isEditable(section != null ? section.getIsEditable() : null)
                .status(sr.getStatus())
                .savedAt(sr.getSavedAt())
                .answers(answerDTOs)
                .build();
    }
}
