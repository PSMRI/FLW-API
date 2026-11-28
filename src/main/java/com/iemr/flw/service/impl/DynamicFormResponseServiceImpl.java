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
import com.iemr.flw.dto.iemr.CompletedBeneficiaryDetailDTO;
import com.iemr.flw.dto.iemr.FormResponseDTO;
import com.iemr.flw.dto.iemr.FormResponseRequest;
import com.iemr.flw.dto.iemr.QuestionAnswerRequest;
import com.iemr.flw.dto.iemr.QuestionResponseDTO;
import com.iemr.flw.dto.iemr.SectionAnswerRequest;
import com.iemr.flw.dto.iemr.SectionResponseDTO;
import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.masterEnum.FormResponseStatus;
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
import com.iemr.flw.masterEnum.SectionPhase;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DynamicFormResponseService;
import com.iemr.flw.service.impl.ResponseDefinitionResolver.VersionDefinition;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    private static final String SECTION_STATUS_DONE = "DONE";
    private static final String SECTION_STATUS_REFUSED = "REFUSED";

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
    private final ConsentRefusalEvaluator consentRefusalEvaluator;
    private final ConditionalAnswerPruner conditionalAnswerPruner;
    private final ResponseDefinitionResolver definitionResolver;

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
            if (FormResponseStatus.COMPLETE.name().equals(formResponse.getStatus())) {
                throw new IllegalStateException(
                        "Cannot update a COMPLETE response (responseId=" + request.getResponseId() + ")");
            }
            // Re-submit stays on the version the response was created on, even if a newer one is latest now:
            // its other sections' stored answers (option UUIDs) belong to that version, and reads resolve the
            // response against it. A response with no recorded version is moved to the latest one.
            FormVersion recorded = formResponse.getVersionNumber() == null ? null
                    : formVersionRepo.findFirstByDynamicForm_FormUuidAndVersionNumberOrderByVersionIdDesc(
                            formResponse.getFormUuid(), formResponse.getVersionNumber()).orElse(null);
            if (recorded != null) {
                version = recorded;
            } else {
                formResponse.setFormUuid(request.getFormUuid());
                formResponse.setVersionNumber(version.getVersionNumber());
            }
            // Bump submittedAt to reflect the re-submission time
            formResponse.setStatus(FormResponseStatus.SUBMITTED.name());
            formResponse.setSubmittedAt(now);
            formResponse.setLastFollowUpAt(now);
            formResponse = formResponseRepo.save(formResponse);
        } else {
            formResponse = createFormResponse(request, version, FormResponseStatus.SUBMITTED.name(), now, vanID, parkingPlaceID);
        }
        return processSections(formResponse, request, version, null, false, vanID, parkingPlaceID);
    }

    @Override
    @Transactional
    public FormResponseDTO completeForm(FormResponseRequest request, String jwtToken) throws IEMRException {
        String actor = jwtUtil.extractUserId(jwtToken).toString();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();

        FormVersion version = resolveLatestVersion(request.getFormUuid());
        Timestamp now = new Timestamp(System.currentTimeMillis());

        List<FormResponse> existing = formResponseRepo.findByBeneficiaryIdAndFormUuidAndVersionNumber(
                request.getBeneficiaryId(), request.getFormUuid(), version.getVersionNumber());
        FormResponse formResponse = existing.isEmpty()
                ? createFormResponse(request, version, FormResponseStatus.SUBMITTED.name(), now, vanID, parkingPlaceID)
                : existing.get(0);

        formResponse.setUpdatedBy(actor);
        formResponse.setStatus(consentRefusalEvaluator.isConsentRefused(request)
                ? FormResponseStatus.REFUSED.name()
                : FormResponseStatus.COMPLETE.name());
        formResponse.setCompletedAt(now);
        formResponse = formResponseRepo.save(formResponse);
        return processSections(formResponse, request, version, actor, true, vanID, parkingPlaceID);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormResponseDTO> getResponsesByBeneficiary(Long beneficiaryId, Integer villageId,
            Integer providerServiceMapId, String formUuid) {
        if (beneficiaryId == null && villageId == null && providerServiceMapId == null) {
            throw new IllegalArgumentException(
                    "At least one of beneficiaryId, villageId, or providerServiceMapId must be provided");
        }
        resolveLatestVersion(formUuid); // fails fast for an unknown form

        List<FormResponse> responses = beneficiaryId != null
                ? formResponseRepo.findByBeneficiaryIdAndFormUuid(beneficiaryId, formUuid)
                : formResponseRepo.findByFormUuidFiltered(formUuid, villageId, providerServiceMapId);

        // Responses may belong to older versions than the latest one; each is read against its own.
        Map<String, VersionDefinition> definitions = new HashMap<>();
        return responses.stream()
                .map(r -> toDTO(r, definitions))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FormResponseDTO getResponseById(Long responseId) {
        FormResponse formResponse = formResponseRepo.findById(responseId)
                .orElseThrow(() -> new NoSuchElementException("FormResponse not found: " + responseId));
        return toDTO(formResponse, new HashMap<>());
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
                .formUuid(version.getDynamicForm().getFormUuid())
                .versionNumber(version.getVersionNumber())
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
        formResponse = formResponseRepo.save(formResponse);
        formResponseRepo.updateVanSerialNo(formResponse.getResponseId(), formResponse.getResponseId());
        // Keep the managed entity in step: a later save/flush of it (e.g. completeForm) would
        // otherwise write vanSerialNo back to NULL.
        formResponse.setVanSerialNo(formResponse.getResponseId());
        return formResponse;
    }

    /**
     * Batch-loads all sections and questions for the version, then processes each answered section.
     */
    private FormResponseDTO processSections(
            FormResponse formResponse,
            FormResponseRequest request,
            FormVersion version,
            String actor,
            boolean applyRefusalRule,
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
        Map<Long, String> questionUuidById = sectionQuestionRepo
                .findAllByVersionIdIncludingInactive(version.getVersionId()).stream()
                .collect(Collectors.toMap(SectionQuestion::getQuestionId, SectionQuestion::getQuestionUuid));
        List<QuestionOption> allOptions =
                questionOptionRepo.findByQuestionIdsOrderByDisplayOrderAsc(questionIds);

        // Map: questionId → (optionValue → QuestionOption)
        Map<Long, Map<String, QuestionOption>> optionsByQuestion = allOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getSectionQuestion().getQuestionId(),
                        Collectors.toMap(QuestionOption::getOptionValue, Function.identity())));

        Map<Long, Set<Long>> childrenByParent =
                conditionalAnswerPruner.loadChildrenByParent(version.getVersionId());
        VersionDefinition responseDefinition = definitionResolver.load(
                formResponse.getFormUuid(), formResponse.getVersionNumber(), new HashMap<>());

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

            String desiredStatus = applyRefusalRule
                    ? consentRefusalEvaluator.determineSectionStatus(
                            section, sectionReq.getAnswers(), SECTION_STATUS_DONE, SECTION_STATUS_REFUSED)
                    : SECTION_STATUS_DONE;
            Optional<SectionResponse> sectionResponseOpt = upsertSectionResponse(
                    formResponse, section, actor, desiredStatus, vanID, parkingPlaceID);
            if (sectionResponseOpt.isEmpty()) {
                continue;
            }
            SectionResponse sectionResponse = sectionResponseOpt.get();

            if (section.getSectionPhase() == SectionPhase.POST_SUBMIT) {
                formResponse.setLastFollowUpAt(sectionResponse.getSavedAt());
                formResponseRepo.save(formResponse);
            }

            Map<String, SectionQuestion> questionMap =
                    questionsBySection.getOrDefault(section.getSectionId(), Map.of());

            List<QuestionResponse> questionResponses = processAnswers(
                    sectionResponse,
                    sectionReq.getAnswers(),
                    questionMap,
                    optionsByQuestion,
                    childrenByParent,
                    questionUuidById,
                    actor,
                    vanID,
                    parkingPlaceID);

            questionResponses = questionResponseRepo.saveAll(questionResponses);
            for (QuestionResponse qr : questionResponses) {
                questionResponseRepo.updateVanSerialNo(qr.getQuestionResponseId(), qr.getQuestionResponseId());
            }

            sectionDTOs.add(buildSectionResponseDTO(sectionResponse, questionResponses, responseDefinition));
        }

        return buildFormResponseDTO(formResponse, responseDefinition, sectionDTOs);
    }

    private Optional<SectionResponse> upsertSectionResponse(
            FormResponse formResponse, FormSection section, String actor, String status, Integer vanID, Integer parkingPlaceID) {
        Long responseId = formResponse.getResponseId();
        if (section.getSectionPhase() != SectionPhase.POST_SUBMIT) {
            Optional<SectionResponse> existing =
                    sectionResponseRepo.findByResponseUuidAndSectionUuid(formResponse.getResponseUuid(), section.getSectionUuid());
            if (existing.isPresent()) {
                if (Boolean.FALSE.equals(section.getIsEditable())) {
                    log.info("Section '{}' is not editable — skipping update for responseId={}",
                            section.getSectionUuid(), responseId);
                    return Optional.empty();
                }
                SectionResponse sr = existing.get();
                sr.setStatus(status);
                sr.setSavedAt(new Timestamp(System.currentTimeMillis()));
                sr.setUpdatedBy(actor);
                if (sr.getVanID() == null) { sr.setVanID(vanID); sr.setParkingPlaceID(parkingPlaceID); }
                SectionResponse updated = sectionResponseRepo.save(sr);
                sectionResponseRepo.updateVanSerialNo(updated.getSectionResponseId(), updated.getSectionResponseId());
                return Optional.of(updated);
            }
        }
        // POST_SUBMIT sections always create a new instance — this is what makes repeatable
        // follow-up visits possible; the read path already returns every row for a section.
        SectionResponse sr = SectionResponse.builder()
                .responseId(responseId)
                .responseUuid(formResponse.getResponseUuid())
                .sectionUuid(section.getSectionUuid())
                .status(status)
                .savedAt(new Timestamp(System.currentTimeMillis()))
                .createdBy(actor)
                .updatedBy(actor)
                .vanID(vanID)
                .parkingPlaceID(parkingPlaceID)
                .build();
        sr = sectionResponseRepo.save(sr);
        sectionResponseRepo.updateVanSerialNo(sr.getSectionResponseId(), sr.getSectionResponseId());
        return Optional.of(sr);
    }

    private List<QuestionResponse> processAnswers(
            SectionResponse sectionResponse,
            List<QuestionAnswerRequest> answers,
            Map<String, SectionQuestion> questionMap,
            Map<Long, Map<String, QuestionOption>> optionsByQuestion,
            Map<Long, Set<Long>> childrenByParent,
            Map<Long, String> questionUuidById,
            String actor,
            Integer vanID,
            Integer parkingPlaceID) {

        // Pass 1: clear old answers of every received question and its SHOW_QUESTION descendants,
        // so children hidden by an edited parent don't keep stale answers. Done up-front so a
        // parent listed after its child can't wipe the child's freshly inserted answer.
        Long sectionResponseId = sectionResponse.getSectionResponseId();
        conditionalAnswerPruner.deleteWithDescendants(sectionResponse.getSectionResponseUuid(),
                answeredQuestionIds(answers, questionMap), childrenByParent, questionUuidById);

        // Pass 2: insert the received answers
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

            if (type == QuestionType.RADIO || type == QuestionType.CHECKBOX || type == QuestionType.DROPDOWN) {
                if (answer.getOptionValue() != null) {
                    QuestionOption opt = resolveOption(
                            optionsByQuestion, questionId, answer.getOptionValue(), answer.getQuestionUuid());
                    results.add(QuestionResponse.builder()
                            .sectionResponseId(sectionResponseId)
                            .sectionResponseUuid(sectionResponse.getSectionResponseUuid())
                            .questionUuid(question.getQuestionUuid())
                            .optionUuid(opt != null ? opt.getOptionUuid() : null)
                            .createdBy(actor)
                            .updatedBy(actor)
                            .vanID(vanID)
                            .parkingPlaceID(parkingPlaceID)
                            .build());
                }
            } else if (type == QuestionType.MCQ || type == QuestionType.CHECKBOX_MULTI
                    || type == QuestionType.DROPDOWN_MULTI) {
                if (answer.getOptionValues() != null) {
                    for (String val : answer.getOptionValues()) {
                        QuestionOption opt = resolveOption(
                                optionsByQuestion, questionId, val, answer.getQuestionUuid());
                        results.add(QuestionResponse.builder()
                                .sectionResponseId(sectionResponseId)
                                .sectionResponseUuid(sectionResponse.getSectionResponseUuid())
                                .questionUuid(question.getQuestionUuid())
                                .optionUuid(opt != null ? opt.getOptionUuid() : null)
                                .createdBy(actor)
                                .updatedBy(actor)
                                .vanID(vanID)
                                .parkingPlaceID(parkingPlaceID)
                                .build());
                    }
                }
            } else {
                // TEXT, DATE, AUTO_FILL, NUMBER_PICKER — prefer answerText, then answerDate, then optionValue (legacy)
                String value = answer.getAnswerText() != null ? answer.getAnswerText()
                        : answer.getAnswerDate() != null ? answer.getAnswerDate()
                        : answer.getOptionValue();
                results.add(QuestionResponse.builder()
                        .sectionResponseId(sectionResponseId)
                        .sectionResponseUuid(sectionResponse.getSectionResponseUuid())
                        .questionUuid(question.getQuestionUuid())
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

    /** Ids of the non-DISPLAY questions in the request; unknown/null uuids are left to processAnswers to reject. */
    private Set<Long> answeredQuestionIds(List<QuestionAnswerRequest> answers, Map<String, SectionQuestion> questionMap) {
        return answers.stream()
                .map(a -> a.getQuestionUuid() == null ? null : questionMap.get(a.getQuestionUuid()))
                .filter(q -> q != null && q.getQuestionType() != QuestionType.DISPLAY)
                .map(SectionQuestion::getQuestionId)
                .collect(Collectors.toSet());
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

    @Override
    @Transactional(readOnly = true)
    public Map<Long, FormResponseDTO> getResponsesByIds(Collection<Long> responseIds) {
        Map<String, VersionDefinition> definitions = new HashMap<>();
        Map<Long, FormResponseDTO> out = new LinkedHashMap<>();
        for (FormResponse r : formResponseRepo.findAllById(responseIds)) {
            out.put(r.getResponseId(), toDTO(r, definitions));
        }
        return out;
    }

    /** Full DTO of a stored response, read against the form version it was captured on. */
    private FormResponseDTO toDTO(FormResponse r, Map<String, VersionDefinition> definitions) {
        VersionDefinition definition = definitionResolver.load(r.getFormUuid(), r.getVersionNumber(), definitions);
        if (definition == null) {
            log.warn("Form {} version {} not found on this machine; responseId={} returned without definition IDs",
                    r.getFormUuid(), r.getVersionNumber(), r.getResponseId());
        }
        return buildFormResponseDTO(r, definition, loadSectionResponseDTOs(r.getResponseUuid(), definition));
    }

    private List<SectionResponseDTO> loadSectionResponseDTOs(String responseUuid, VersionDefinition definition) {
        if (responseUuid == null) {
            return List.of();
        }
        List<SectionResponse> sections = sectionResponseRepo.findByResponseUuid(responseUuid);
        if (sections.isEmpty()) {
            return List.of();
        }
        Set<String> srUuids = sections.stream()
                .map(SectionResponse::getSectionResponseUuid)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, List<QuestionResponse>> answersBySectionResponse = srUuids.isEmpty() ? Map.of()
                : questionResponseRepo.findBySectionResponseUuidIn(srUuids).stream()
                        .collect(Collectors.groupingBy(QuestionResponse::getSectionResponseUuid));

        return sections.stream()
                .map(sr -> buildSectionResponseDTO(
                        sr,
                        sr.getSectionResponseUuid() == null ? List.of()
                                : answersBySectionResponse.getOrDefault(sr.getSectionResponseUuid(), List.of()),
                        definition))
                .collect(Collectors.toList());
    }

    /**
     * The numeric formId / versionId in the DTO are this machine's IDs, looked up by UUID, so clients
     * keep receiving them; null when the definition is not on this machine.
     */
    private FormResponseDTO buildFormResponseDTO(FormResponse r, VersionDefinition definition,
                                                 List<SectionResponseDTO> sections) {
        FormVersion version = definition != null ? definition.version() : null;
        return FormResponseDTO.builder()
                .responseId(r.getResponseId())
                .responseUuid(r.getResponseUuid())
                .beneficiaryId(r.getBeneficiaryId())
                .formUuid(r.getFormUuid())
                .formId(version != null ? version.getDynamicForm().getFormId() : null)
                .versionId(version != null ? version.getVersionId() : null)
                .versionNumber(r.getVersionNumber())
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
            SectionResponse sr, List<QuestionResponse> answers, VersionDefinition definition) {
        List<QuestionResponseDTO> answerDTOs = answers.stream()
                .map(a -> {
                    SectionQuestion question = definition != null ? definition.question(a.getQuestionUuid()) : null;
                    QuestionOption option = definition != null
                            ? definition.option(a.getQuestionUuid(), a.getOptionUuid()) : null;
                    return QuestionResponseDTO.builder()
                            .questionResponseId(a.getQuestionResponseId())
                            .questionId(question != null ? question.getQuestionId() : null)
                            .questionUuid(a.getQuestionUuid())
                            .optionId(option != null ? option.getOptionId() : null)
                            .optionValue(option != null ? option.getOptionValue() : null)
                            .optionUuid(a.getOptionUuid())
                            .answerText(a.getAnswerText())
                            .build();
                })
                .collect(Collectors.toList());
        FormSection section = definition != null ? definition.section(sr.getSectionUuid()) : null;
        return SectionResponseDTO.builder()
                .sectionResponseId(sr.getSectionResponseId())
                .sectionId(section != null ? section.getSectionId() : null)
                .sectionUuid(sr.getSectionUuid())
                .isEditable(section != null ? section.getIsEditable() : null)
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
    public List<FormResponseDTO> findPendingFollowUps(List<String> formUuids, int delayDays) {
        if (formUuids == null || formUuids.isEmpty()) {
            return List.of();
        }
        LocalDate target = LocalDate.now().minusDays(delayDays);
        Timestamp windowStart = Timestamp.valueOf(target.atStartOfDay());
        Timestamp windowEnd   = Timestamp.valueOf(target.plusDays(1).atStartOfDay());
        return formResponseRepo.findPendingFollowUps(formUuids, windowStart, windowEnd)
                .stream()
                .map(r -> buildFormResponseDTO(r, null, List.of()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompletedBeneficiaryDetailDTO> getCompletedBeneficiaries(
            FormType formType, Integer villageId, Integer providerServiceMapId, SectionPhase sectionPhase) {
        DynamicForm form = dynamicFormRepo.findByFormTypeAndIsActive(formType, true)
                .orElseThrow(() -> new NoSuchElementException(
                        "No active form found for type: " + formType));

        List<String> statuses = List.of(FormResponseStatus.COMPLETE.name(), FormResponseStatus.REFUSED.name(), FormResponseStatus.SUBMITTED.name());
        List<FormResponse> responses = (villageId != null || providerServiceMapId != null)
                ? formResponseRepo.findByFormUuidAndStatusInFiltered(form.getFormUuid(), statuses, villageId, providerServiceMapId)
                : formResponseRepo.findByFormUuidAndStatusIn(form.getFormUuid(), statuses);

        if (responses.isEmpty()) {
            return List.of();
        }

        List<Long> responseIds = responses.stream().map(FormResponse::getResponseId).collect(Collectors.toList());
        Map<Long, Integer> sectionsFilledByResponseId = sectionResponseRepo
                .countByResponseIdInAndSectionPhase(responseIds, sectionPhase).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));

        Map<Integer, Integer> totalSectionsByVersionNumber = formSectionRepo
                .countByFormUuidAndSectionPhaseGroupByVersionNumber(form.getFormUuid(), sectionPhase).stream()
                .collect(Collectors.toMap(row -> (Integer) row[0], row -> ((Number) row[1]).intValue(), Integer::sum));

        return responses.stream()
                .map(r -> CompletedBeneficiaryDetailDTO.builder()
                        .beneficiaryId(r.getBeneficiaryId())
                        .isRefused(FormResponseStatus.REFUSED.name().equals(r.getStatus()))
                        .sectionsFilled(sectionsFilledByResponseId.getOrDefault(r.getResponseId(), 0))
                        .totalSections(totalSectionsByVersionNumber.getOrDefault(r.getVersionNumber(), 0))
                        .build())
                .collect(Collectors.toList());
    }
}
