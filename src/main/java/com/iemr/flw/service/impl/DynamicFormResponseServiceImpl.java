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
import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.masterEnum.FormType;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.repo.iemr.FormResponseRepo;
import com.iemr.flw.repo.iemr.FormSectionRepo;
import com.iemr.flw.repo.iemr.FormVersionRepo;
import com.iemr.flw.repo.iemr.QuestionOptionRepo;
import com.iemr.flw.repo.iemr.QuestionResponseRepo;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.repo.iemr.SectionResponseRepo;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DynamicFormResponseService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles save, submit, complete, and fetch of dynamic form responses.
 * Resolves the latest FormVersion server-side so the client only needs to send formUuid.
 *
 * @author Piramal Swasthya
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DynamicFormResponseServiceImpl implements DynamicFormResponseService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_SUBMITTED = "SUBMITTED";
    private static final String STATUS_COMPLETE = "COMPLETE";
    private static final String SECTION_STATUS_DONE = "DONE";

    private final DynamicFormRepo dynamicFormRepo;
    private final FormResponseRepo formResponseRepo;
    private final SectionResponseRepo sectionResponseRepo;
    private final QuestionResponseRepo questionResponseRepo;
    private final FormVersionRepo formVersionRepo;
    private final FormSectionRepo formSectionRepo;
    private final SectionQuestionRepo sectionQuestionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final FormResponseItemSaver itemSaver;
    private final JwtUtil jwtUtil;
    private final CampConfigService campConfigService;

    @Override
    @Transactional
    public FormResponseDTO submitForm(FormResponseRequest request) {
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        FormVersion version = resolveLatestVersion(request.getFormUuid());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        FormResponse formResponse;
        if (request.getResponseId() != null) {
            // Re-submit: update existing SUBMITTED response's sections
            formResponse = formResponseRepo.findById(request.getResponseId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "FormResponse not found: " + request.getResponseId()));
            if (STATUS_COMPLETE.equals(formResponse.getStatus())) {
                throw new IllegalStateException(
                        "Cannot update a COMPLETE response (responseId=" + request.getResponseId() + ")");
            }
            // Bump submittedAt to reflect the re-submission time
            formResponse.setStatus(STATUS_SUBMITTED);
            formResponse.setSubmittedAt(now);
            formResponse.setLastFollowUpAt(now);
            formResponse = formResponseRepo.save(formResponse);
        } else {
            formResponse = createFormResponse(request, version, STATUS_SUBMITTED, now, vanID, parkingPlaceID);
        }
        return processSections(formResponse, request, version, null, vanID, parkingPlaceID);
    }

    @Override
    @Transactional
    public FormResponseDTO completeForm(FormResponseRequest request, String jwtToken) throws IEMRException {
        String actor = jwtUtil.extractUserId(jwtToken).toString();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        FormVersion version = resolveLatestVersion(request.getFormUuid());
        Long formId = version.getDynamicForm().getFormId();

        List<FormResponse> existing =
                formResponseRepo.findByBeneficiaryIdAndFormId(request.getBeneficiaryId(), formId);
        if (existing.isEmpty()) {
            throw new NoSuchElementException(
                    "FormResponse not found for beneficiaryId=" + request.getBeneficiaryId()
                    + " formUuid=" + request.getFormUuid());
        }
        FormResponse formResponse = existing.get(0);

        formResponse.setUpdatedBy(actor);
        formResponse.setStatus(STATUS_COMPLETE);
        formResponse.setCompletedAt(new Timestamp(System.currentTimeMillis()));
        formResponse = formResponseRepo.save(formResponse);
        return processSections(formResponse, request, version, actor, vanID, parkingPlaceID);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormResponseDTO> getResponsesByBeneficiary(Long beneficiaryId, String formUuid) {
        FormVersion version = resolveLatestVersion(formUuid);
        Long formId = version.getDynamicForm().getFormId();

        List<FormResponse> responses =
                formResponseRepo.findByBeneficiaryIdAndFormId(beneficiaryId, formId);
        return responses.stream()
                .map(r -> buildFormResponseDTO(r, loadSectionResponseDTOs(r.getResponseId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FormResponseDTO getResponseById(Long responseId) {
        FormResponse formResponse = formResponseRepo.findById(responseId)
                .orElseThrow(() -> new NoSuchElementException("FormResponse not found: " + responseId));
        return buildFormResponseDTO(formResponse, loadSectionResponseDTOs(responseId));
    }

    // ---- private helpers ----

    private FormVersion resolveLatestVersion(String formUuid) {
        return formVersionRepo.findByDynamicForm_FormUuidAndIsLatest(formUuid, true)
                .orElseThrow(() -> new NoSuchElementException(
                        "No active FormVersion found for form code: " + formUuid));
    }

    /** Creates a new FormResponse row. */
    private FormResponse createFormResponse(
            FormResponseRequest request,
            FormVersion version,
            String status,
            Timestamp submittedAt,
            Integer vanID,
            Integer parkingPlaceID) {

        FormResponse formResponse = FormResponse.builder()
                .beneficiaryId(request.getBeneficiaryId())
                .formId(version.getDynamicForm().getFormId())
                .versionId(version.getVersionId())
                .officerId(request.getOfficerId())
                .status(status)
                .submittedAt(submittedAt)
                .lastFollowUpAt(submittedAt)
                // TODO: set audit fields when auth is re-enabled for /dynamicForm/response endpoints
                // .createdBy(jwtUtil.extractUsername(authorization))
                // .updatedBy(jwtUtil.extractUsername(authorization))
                .vanID(vanID)
                .parkingPlaceID(parkingPlaceID)
                .build();
        return formResponseRepo.save(formResponse);
    }

    /**
     * Batch-loads all sections and questions for the version, then processes each answered section.
     */
    private FormResponseDTO processSections(
            FormResponse formResponse,
            FormResponseRequest request,
            FormVersion version,
            String actor,
            Integer vanID,
            Integer parkingPlaceID) {

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

        // Map: questionId → (optionValue → QuestionOption)
        Map<Long, Map<String, QuestionOption>> optionsByQuestion = allOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getSectionQuestion().getQuestionId(),
                        Collectors.toMap(QuestionOption::getOptionValue, Function.identity())));

        List<SectionResponseDTO> sectionDTOs = new ArrayList<>();
        for (SectionAnswerRequest sectionReq : request.getSections()) {
            if (sectionReq.getSectionUuid() == null) {
                throw new IllegalArgumentException(
                        "sectionUuid is null — did you send sectionCode instead?");
            }
            FormSection section = sectionByUuid.get(sectionReq.getSectionUuid());
            if (section == null) {
                throw new NoSuchElementException(
                        "Section '" + sectionReq.getSectionUuid() +
                        "' not found in form '" + request.getFormUuid() + "'");
            }

            SectionResponse sectionResponse = upsertSectionResponse(
                    formResponse.getResponseId(), section.getSectionId(), actor, vanID, parkingPlaceID);

            if ("POST_SUBMIT".equals(section.getSectionPhase())) {
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
                    actor,
                    vanID,
                    parkingPlaceID);

            questionResponseRepo.saveAll(questionResponses);

            sectionDTOs.add(buildSectionResponseDTO(sectionResponse, questionResponses));
        }

        return buildFormResponseDTO(formResponse, sectionDTOs);
    }

    private SectionResponse upsertSectionResponse(Long responseId, Long sectionId, String actor,
            Integer vanID, Integer parkingPlaceID) {
        Optional<SectionResponse> existing =
                sectionResponseRepo.findByResponseIdAndSectionId(responseId, sectionId);
        if (existing.isPresent()) {
            SectionResponse sr = existing.get();
            sr.setStatus(SECTION_STATUS_DONE);
            sr.setSavedAt(new Timestamp(System.currentTimeMillis()));
            sr.setUpdatedBy(actor);
            if (sr.getVanID() == null) { sr.setVanID(vanID); sr.setParkingPlaceID(parkingPlaceID); }
            return sectionResponseRepo.save(sr);
        }
        SectionResponse sr = SectionResponse.builder()
                .responseId(responseId)
                .sectionId(sectionId)
                .status(SECTION_STATUS_DONE)
                .savedAt(new Timestamp(System.currentTimeMillis()))
                .createdBy(actor)
                .updatedBy(actor)
                .vanID(vanID)
                .parkingPlaceID(parkingPlaceID)
                .build();
        return sectionResponseRepo.save(sr);
    }

    private List<QuestionResponse> processAnswers(
            Long sectionResponseId,
            List<QuestionAnswerRequest> answers,
            Map<String, SectionQuestion> questionMap,
            Map<Long, Map<String, QuestionOption>> optionsByQuestion,
            String actor,
            Integer vanID,
            Integer parkingPlaceID) {

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
                // DISPLAY questions carry no answer data
                continue;
            }

            // Delete any existing answers for this question in this section (handles re-saves)
            questionResponseRepo.deleteByQuestionIdAndSectionResponseId(questionId, sectionResponseId);

            if (type == QuestionType.RADIO) {
                if (answer.getOptionValue() != null) {
                    QuestionOption opt = resolveOption(
                            optionsByQuestion, questionId, answer.getOptionValue(), answer.getQuestionUuid());
                    results.add(QuestionResponse.builder()
                            .sectionResponseId(sectionResponseId)
                            .questionId(questionId)
                            .optionId(opt != null ? opt.getOptionId() : null)
                            .createdBy(actor)
                            .updatedBy(actor)
                            .vanID(vanID)
                            .parkingPlaceID(parkingPlaceID)
                            .build());
                }
            } else if (type == QuestionType.MCQ) {
                if (answer.getOptionValues() != null) {
                    for (String val : answer.getOptionValues()) {
                        QuestionOption opt = resolveOption(
                                optionsByQuestion, questionId, val, answer.getQuestionUuid());
                        results.add(QuestionResponse.builder()
                                .sectionResponseId(sectionResponseId)
                                .questionId(questionId)
                                .optionId(opt != null ? opt.getOptionId() : null)
                                .createdBy(actor)
                                .updatedBy(actor)
                                .vanID(vanID)
                                .parkingPlaceID(parkingPlaceID)
                                .build());
                    }
                }
            } else {
                // TEXT, DATE, AUTO_FILL — prefer answerText, then answerDate, then optionValue (legacy)
                String value = answer.getAnswerText() != null ? answer.getAnswerText()
                        : answer.getAnswerDate() != null ? answer.getAnswerDate()
                        : answer.getOptionValue();
                results.add(QuestionResponse.builder()
                        .sectionResponseId(sectionResponseId)
                        .questionId(questionId)
                        .answerText(value)
                        .createdBy(actor)
                        .updatedBy(actor)
                        .vanID(vanID)
                        .parkingPlaceID(parkingPlaceID)
                        .build());
            }
        }
        return results;
    }

    private QuestionOption resolveOption(
            Map<Long, Map<String, QuestionOption>> optionsByQuestion,
            Long questionId,
            String optionValue,
            String questionUuid) {

        Map<String, QuestionOption> opts = optionsByQuestion.get(questionId);
        if (opts == null || !opts.containsKey(optionValue)) {
            log.warn("Option value '{}' not found for question '{}', storing null optionId",
                    optionValue, questionUuid);
            return null;
        }
        return opts.get(optionValue);
    }

    // ---- read helpers ----

    private List<SectionResponseDTO> loadSectionResponseDTOs(Long responseId) {
        List<SectionResponse> sections = sectionResponseRepo.findByResponseId(responseId);
        if (sections.isEmpty()) {
            return List.of();
        }
        Set<Long> srIds = sections.stream()
                .map(SectionResponse::getSectionResponseId)
                .collect(Collectors.toSet());
        List<QuestionResponse> allAnswers =
                questionResponseRepo.findBySectionResponseIdIn(srIds);
        Map<Long, List<QuestionResponse>> answersBySectionResponse = allAnswers.stream()
                .collect(Collectors.groupingBy(QuestionResponse::getSectionResponseId));

        return sections.stream()
                .map(sr -> buildSectionResponseDTO(
                        sr,
                        answersBySectionResponse.getOrDefault(sr.getSectionResponseId(), List.of())))
                .collect(Collectors.toList());
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
            SectionResponse sr, List<QuestionResponse> answers) {
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
                .status(sr.getStatus())
                .savedAt(sr.getSavedAt())
                .answers(answerDTOs)
                .build();
    }

    @Override
    public List<FormResponseDTO> submitBulk(List<FormResponseRequest> requests, String jwtToken) {
        if (requests == null || requests.isEmpty()) return List.of();
        List<FormResponseDTO> results = new ArrayList<>();
        for (FormResponseRequest req : requests) {
            try {
                results.add(itemSaver.saveForBulk(req, jwtToken));
            } catch (Exception ex) {
                log.warn("submitBulk: item skipped for beneficiaryId={} — {}",
                        req.getBeneficiaryId(), ex.getMessage());
            }
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormResponseDTO> findPendingFollowUps(List<Long> formIds, int delayDays) {
        if (formIds == null || formIds.isEmpty()) {
            return List.of();
        }
        LocalDate target = LocalDate.now().minusDays(delayDays);
        Timestamp windowStart = Timestamp.valueOf(target.atStartOfDay());
        Timestamp windowEnd   = Timestamp.valueOf(target.plusDays(1).atStartOfDay());
        return formResponseRepo.findPendingFollowUps(formIds, windowStart, windowEnd)
                .stream()
                .map(r -> buildFormResponseDTO(r, List.of()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCompletedBeneficiaries(FormType formType) {
        DynamicForm form = dynamicFormRepo.findByFormTypeAndIsActive(formType, true)
                .orElseThrow(() -> new NoSuchElementException(
                        "No active form found for type: " + formType));
        return formResponseRepo.findBeneficiaryIdsByFormIdAndStatus(form.getFormId(), STATUS_COMPLETE);
    }
}
