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

import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.masterEnum.ValidationType;
import com.iemr.flw.repo.iemr.SectionQuestionRepo;
import com.iemr.flw.service.DynamicFormReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adds counsellor guidance to the TB Counselling V2 form's "Government Schemes" section (TB2_SEC_C),
 * so the same points are explained to every patient. The changes go into a new version 2 (via
 * {@link FormVersionCloner}); version 1 keeps the seeded structure, so responses submitted against it
 * keep their meaning.
 *
 * 1. Adds helper text under "Nikshay Poshan Yojana (NPY) eligibility explained" (TB2_C_Q1) and
 *    "DOTS treatment" (TB2_C_Q2): one {@link ValidationType#HELPER_TEXT_ENGLISH} row per English bullet
 *    and one {@link ValidationType#HELPER_TEXT_HINDI} row per Hindi bullet, inserted in display order
 *    so the client shows them in validationId order. Each bullet fits validationParam's 255 chars, so
 *    no schema change is needed; errorMessage (NOT NULL) carries the same text, as UNIT rows do.
 * 2. Renames TB2_C_Q2 from "DOTS free treatment explained" to "DOTS treatment", per the spec.
 * 3. Flags both changed questions (TB2_C_Q1, TB2_C_Q2) with requiresBackFill, so the client asks for them
 *    on beneficiaries whose earlier responses were captured on version 1.
 *
 * Unchanged: Section C still opens only when "Has the beneficiary agreed for counselling" = Yes, keeps
 * its single "Select All", and both checkboxes stay mandatory.
 *
 * Every step is idempotent (version 2 is reused once created, ensureValidation matches on type + param,
 * same-value update), so a re-run is a no-op.
 */
@Component
@RequiredArgsConstructor
public class V012_AddTbCounsellingSchemeHelperText implements FormStructureMigration {

    private static final String FORM_UUID = "TB_COUNSELLING_V2";
    private static final String SECTION_UUID = "TB2_SEC_C";
    private static final String NPY_QUESTION_UUID = "TB2_C_Q1";
    private static final String DOTS_QUESTION_UUID = "TB2_C_Q2";
    private static final String DOTS_NEW_TEXT = "DOTS treatment";
    private static final String DOTS_NEW_TEXT_HINDI = "DOTS उपचार";
    private static final int TARGET_VERSION = 2;

    private static final List<String> NPY_HELPER_ENGLISH = List.of(
            "NPY: DBT of Rs. 1000/month for the duration of treatment.",
            "Cash transfer (DBT) in the patient's Aadhaar-linked bank account, credited monthly for the full treatment duration.",
            "Requirements: Aadhaar-linked bank account and correct patient details in Nikshay.",
            "No separate application needed if enrolled in Nikshay.");

    private static final List<String> NPY_HELPER_HINDI = List.of(
            "NPY: उपचार की पूरी अवधि के दौरान ₹1000 प्रति माह का DBT।",
            "नकद हस्तांतरण (DBT) मरीज़ के आधार से जुड़े बैंक खाते में, पूरी उपचार अवधि तक हर महीने जमा किया जाता है।",
            "आवश्यकताएँ: आधार से जुड़ा बैंक खाता और निक्षय में मरीज़ का सही विवरण।",
            "निक्षय में नामांकित होने पर अलग से आवेदन की आवश्यकता नहीं है।");

    private static final List<String> DOTS_HELPER_ENGLISH = List.of(
            "A health worker, treatment supporter, or family member watches the patient take each dose, so adherence isn't left to memory or self-report.",
            "Provision of free medicine.",
            "Confirm the patient understands that treatment is free under NTEP.");

    private static final List<String> DOTS_HELPER_HINDI = List.of(
            "स्वास्थ्य कार्यकर्ता, उपचार सहायक या परिवार का कोई सदस्य मरीज़ को हर खुराक लेते हुए देखता है, ताकि दवा लेना याददाश्त या खुद की जानकारी पर निर्भर न रहे।",
            "मुफ़्त दवा उपलब्ध कराई जाती है।",
            "पुष्टि करें कि मरीज़ समझता है कि NTEP के तहत इलाज मुफ़्त है।");

    private final SectionQuestionRepo questionRepo;
    private final FormVersionCloner versionCloner;

    @Override
    public String migrationId() {
        return "V012";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = versionCloner.ensureVersion(svc, FORM_UUID, TARGET_VERSION);
        FormSection section = svc.section(version, SECTION_UUID)
                .orElseThrow(() -> new IllegalStateException("Section " + SECTION_UUID + " not found"));

        SectionQuestion npy = requireQuestion(svc, section, NPY_QUESTION_UUID);
        addHelperText(svc, npy, NPY_HELPER_ENGLISH, NPY_HELPER_HINDI);

        SectionQuestion dots = requireQuestion(svc, section, DOTS_QUESTION_UUID);
        dots.setQuestionText(DOTS_NEW_TEXT);
        dots.setQuestionTextHindi(DOTS_NEW_TEXT_HINDI);
        questionRepo.save(dots);
        addHelperText(svc, dots, DOTS_HELPER_ENGLISH, DOTS_HELPER_HINDI);

        flagForBackFill(npy);
        flagForBackFill(dots);
    }

    private void flagForBackFill(SectionQuestion question) {
        question.setRequiresBackFill(true);
        questionRepo.save(question);
    }

    private SectionQuestion requireQuestion(DynamicFormReconciliationService svc, FormSection section, String uuid) {
        return svc.question(section, uuid)
                .orElseThrow(() -> new IllegalStateException("Question " + uuid + " not found"));
    }

    private void addHelperText(DynamicFormReconciliationService svc, SectionQuestion question,
            List<String> english, List<String> hindi) {
        english.forEach(text -> svc.ensureValidation(question, helperText(ValidationType.HELPER_TEXT_ENGLISH, text)));
        hindi.forEach(text -> svc.ensureValidation(question, helperText(ValidationType.HELPER_TEXT_HINDI, text)));
    }

    private QuestionValidationDTO helperText(ValidationType type, String text) {
        QuestionValidationDTO dto = new QuestionValidationDTO();
        dto.setValidationType(type);
        dto.setValidationParam(text);
        dto.setErrorMessage(text);
        return dto;
    }
}
