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
package com.iemr.flw.seeder;

import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import com.iemr.flw.masterEnum.FormType;
import com.iemr.flw.masterEnum.QuestionType;
import com.iemr.flw.masterEnum.SectionPhase;
import com.iemr.flw.repo.iemr.DynamicFormRepo;
import com.iemr.flw.service.DynamicFormDefinitionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Seeds the Contact Follow Up form definition on application startup if it does not already
 * exist. Idempotent: skips creation if the form UUID is already present. If present but inactive,
 * it is reactivated instead (never deleted — a form can have real historical responses
 * referencing its options via a DB-level FK outside JPA's cascade, so deleting it can fail app
 * startup).
 *
 * One-time form (no repeatable follow-up section, unlike {@link TptFollowUpFormSeeder}). Its
 * "Status of Clinical Screening" question's outcomes ("Full Treatment" / "TPT Eligible" /
 * "No Treatment") are each meant to route this record into other modules (the TPT Follow Up
 * form, confirmed-case listing, or no action) — that record-routing logic is out of scope here
 * and not implemented, and no cross-form SHOW_FORM condition is wired for any of them, since
 * seeders must not depend on each other's existence or seed order.
 *
 * @author Piramal Swasthya
 */
@Component
@RequiredArgsConstructor
public class ContactFollowUpFormSeeder {

    private static final Logger log = LoggerFactory.getLogger(ContactFollowUpFormSeeder.class);

    public static final String FORM_UUID = "CONTACT_FOLLOW_UP";

    private final DynamicFormDefinitionService formService;
    private final DynamicFormRepo formRepo;

    @PostConstruct
    public void seed() {
        Optional<DynamicForm> existing = formRepo.findByFormUuid(FORM_UUID);
        if (existing.isPresent()) {
            DynamicForm form = existing.get();
            if (Boolean.FALSE.equals(form.getIsActive())) {
                form.setIsActive(true);
                formRepo.save(form);
                log.info("ContactFollowUpFormSeeder: form '{}' was inactive — reactivated.", FORM_UUID);
            } else {
                log.info("ContactFollowUpFormSeeder: form '{}' already exists and is active — skipping seed.", FORM_UUID);
            }
            return;
        }
        log.info("ContactFollowUpFormSeeder: seeding Contact Follow Up form...");
        formService.createForm(buildFormDto());
        log.info("ContactFollowUpFormSeeder: seed complete.");
    }

    // ── Form Definition ──────────────────────────────────────────────────────────

    private DynamicFormDTO buildFormDto() {
        DynamicFormDTO dto = new DynamicFormDTO();
        dto.setFormUuid(FORM_UUID);
        dto.setFormName("Contact Follow Up");
        dto.setFormType(FormType.CONTACT_FOLLOW_UP);
        dto.setIsActive(true);

        List<FormSectionDTO> sections = new ArrayList<>();
        sections.add(buildScreeningSection());
        dto.setSections(sections);
        return dto;
    }

    // ── Section: Screening & Referral ────────────────────────────────────────────

    private FormSectionDTO buildScreeningSection() {
        FormSectionDTO s = section("CFU_SEC_1", "Screening & Referral", "जांच और रेफरल",
                SectionPhase.PRE_SUBMIT, 1, true, true, true);
        List<SectionQuestionDTO> qs = new ArrayList<>();

        qs.add(yesNoQuestion("CFU_ADVISED_VISIT_FACILITY", "Advised to visit health facility",
                "स्वास्थ्य सुविधा में जाने की सलाह दी गई", 1));

        qs.add(yesNoQuestion("CFU_VISITED_FACILITY", "Have you visited the health facility",
                "क्या आपने स्वास्थ्य सुविधा का दौरा किया", 2));

        // Status of Clinical Screening — "TPT Eligible" opens the TPT Follow Up form.
        // "Full Treatment"/"No Treatment" are meant to route this record elsewhere (confirmed-case
        // listing / no action) — that record-routing is out of scope here, so no condition is wired
        // for those two options.
        SectionQuestionDTO screeningStatus = new SectionQuestionDTO();
        screeningStatus.setQuestionUuid("CFU_CLINICAL_SCREENING_STATUS");
        screeningStatus.setQuestionText("Status of Clinical Screening");
        screeningStatus.setQuestionTextHindi("नैदानिक जांच की स्थिति");
        screeningStatus.setQuestionType(QuestionType.RADIO);
        screeningStatus.setIsMandatory(true);
        screeningStatus.setDisplayOrder(3);
        screeningStatus.setVisibleByDefault(true);
        screeningStatus.setOptions(List.of(
                option("Full Treatment", "पूर्ण उपचार", "FULL_TREATMENT", "पूर्ण उपचार", 1, List.of()),
                option("TPT Eligible", "टीपीटी के लिए पात्र", "TPT_ELIGIBLE", "टीपीटी के लिए पात्र", 2, List.of()),
                option("No Treatment", "कोई उपचार नहीं", "NO_TREATMENT", "कोई उपचार नहीं", 3, List.of())));
        screeningStatus.setValidations(List.of());
        qs.add(screeningStatus);

        s.setQuestions(qs);
        return s;
    }

    // ── Builder Helpers ───────────────────────────────────────────────────────────

    private FormSectionDTO section(String uuid, String name, String nameHindi, SectionPhase phase,
                                    int order, boolean required, boolean hasSubmitButton, boolean isEditable) {
        FormSectionDTO s = new FormSectionDTO();
        s.setSectionUuid(uuid);
        s.setSectionName(name);
        s.setSectionNameHindi(nameHindi);
        s.setSectionPhase(phase);
        s.setDisplayOrder(order);
        s.setIsRequired(required);
        s.setHasSubmitButton(hasSubmitButton);
        s.setIsEditable(isEditable);
        s.setQuestions(new ArrayList<>());
        return s;
    }

    private SectionQuestionDTO yesNoQuestion(String uuid, String text, String textHindi, int order) {
        SectionQuestionDTO q = new SectionQuestionDTO();
        q.setQuestionUuid(uuid);
        q.setQuestionText(text);
        q.setQuestionTextHindi(textHindi);
        q.setQuestionType(QuestionType.RADIO);
        q.setIsMandatory(true);
        q.setDisplayOrder(order);
        q.setVisibleByDefault(true);
        q.setOptions(List.of(
                option("Yes", "हाँ", "YES", "हाँ", 1, List.of()),
                option("No", "नहीं", "NO", "नहीं", 2, List.of())));
        q.setValidations(List.of());
        return q;
    }

    private QuestionOptionDTO option(String label, String labelHindi, String value, String valueHindi,
                                      int order, List<OptionConditionDTO> conditions) {
        QuestionOptionDTO o = new QuestionOptionDTO();
        o.setOptionLabel(label);
        o.setOptionLabelHindi(labelHindi);
        o.setOptionValue(value);
        o.setOptionValueHindi(valueHindi);
        o.setDisplayOrder(order);
        o.setConditions(conditions);
        return o;
    }
}
