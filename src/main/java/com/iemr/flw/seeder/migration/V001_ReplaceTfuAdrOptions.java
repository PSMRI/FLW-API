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
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.service.DynamicFormReconciliationService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Replaces the TPT Follow Up form's "Adverse Drug Reactions" (TFU_ADR) option list with the
 * clinically revised set of symptoms, per the updated ADR checklist supplied for this form.
 *
 * 7 of the 24 existing options already match the new list exactly (same wording) and are left
 * untouched by {@code ensureOption}'s natural-key match: NAUSEA, VOMITING, ABDOMINAL_PAIN,
 * DARK_COLOUR_URINE, ITCHING, TINGLING_BURNING_NUMBNESS_HANDS_FEET, FLU_LIKE_SYNDROME. The other
 * 17 old options are no longer part of the revised list and are soft-removed (never hard-deleted —
 * historical responses may reference them); the 13 new options are added.
 */
@Component
public class V001_ReplaceTfuAdrOptions implements FormStructureMigration {

    private static final List<String> REMOVED_OPTION_VALUES = List.of(
            "UNKNOWN",
            "FLU_LIKE_SYNDROME_WITH_DIZZINESS_HEADACHE",
            "YELLOWISH_DISCOLORATION_SKIN_EYES",
            "DISCOLORATION_BODY_FLUIDS",
            "PALE_STOOL",
            "SKIN_RASH",
            "SLEEPINESS_LETHARGY",
            "PERSISTENT_UNFORMED_WATERY_STOOLS",
            "MENTAL_CHANGES_SIGNS_BLEEDING",
            "CONVULSIONS",
            "ANAEMIA",
            "ARTHRALGIA",
            "DECREASED_APPETITE",
            "HYPOTENSION_SYNCOPE",
            "CONJUCTIVITIES",
            "SHOCK",
            "NONE_REPORTED");

    @Override
    public String migrationId() {
        return "V001";
    }

    @Override
    public void apply(DynamicFormReconciliationService svc) {
        FormVersion version = svc.latestVersion("TPT_FOLLOW_UP")
                .orElseThrow(() -> new IllegalStateException(
                        "TPT_FOLLOW_UP not found — must run after TptFollowUpFormSeeder"));
        FormSection section = svc.section(version, "TFU_SEC_2")
                .orElseThrow(() -> new IllegalStateException("Section TFU_SEC_2 not found"));
        SectionQuestion question = svc.question(section, "TFU_ADR")
                .orElseThrow(() -> new IllegalStateException("Question TFU_ADR not found"));

        REMOVED_OPTION_VALUES.forEach(value -> svc.option(question, value)
                .ifPresent(opt -> svc.removeOption(opt.getOptionId())));

        addOption(svc, question, "Yellowness of Skin", "YELLOWNESS_OF_SKIN");
        addOption(svc, question, "Loose motions >4 times in a day", "LOOSE_MOTIONS_GT4_PER_DAY");
        addOption(svc, question, "Rashes", "RASHES");
        addOption(svc, question, "Pain in Joints", "PAIN_IN_JOINTS");
        addOption(svc, question, "Impaired vision: Pain, Blurring of vision, Disturbance in colour vision", "IMPAIRED_VISION");
        addOption(svc, question, "Swelling of face or legs, Less or no urine", "SWELLING_FACE_LEGS_LESS_NO_URINE");
        addOption(svc, question, "Seeing abnormal things, changes of thoughts, suicidal thoughts", "ABNORMAL_THOUGHTS_SUICIDAL");
        addOption(svc, question, "Tiredness, lethargy, headache, giddiness, pale look, palpitations", "TIREDNESS_LETHARGY_GIDDINESS_PALPITATIONS");
        addOption(svc, question, "Ringing in ears, Loss of hearing, dizziness and loss of balance leading to recurrent fall", "TINNITUS_HEARING_LOSS_DIZZINESS_FALL");
        addOption(svc, question, "Slowness of activities, Swelling of face or neck, dis-appropriate weight gain", "SLOWNESS_FACIAL_NECK_SWELLING_WEIGHT_GAIN");
        addOption(svc, question, "Pain and swelling in muscles and Tendons, difficulty in movement", "MUSCLE_TENDON_PAIN_SWELLING");
        addOption(svc, question, "Convulsion", "CONVULSION");
        addOption(svc, question, "Orange and red color of urine, sweat, sputum, saliva or tears", "ORANGE_RED_DISCOLORATION_BODY_FLUIDS");
    }

    private void addOption(DynamicFormReconciliationService svc, SectionQuestion question, String label, String value) {
        QuestionOptionDTO dto = new QuestionOptionDTO();
        dto.setOptionLabel(label);
        dto.setOptionValue(value);
        svc.ensureOption(question, dto);
    }
}
