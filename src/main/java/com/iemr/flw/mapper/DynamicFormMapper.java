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
package com.iemr.flw.mapper;

import com.iemr.flw.domain.iemr.DynamicForm;
import com.iemr.flw.domain.iemr.FormSection;
import com.iemr.flw.domain.iemr.FormVersion;
import com.iemr.flw.domain.iemr.OptionCondition;
import com.iemr.flw.domain.iemr.QuestionOption;
import com.iemr.flw.domain.iemr.QuestionValidation;
import com.iemr.flw.domain.iemr.SectionQuestion;
import com.iemr.flw.dto.iemr.DynamicFormDTO;
import com.iemr.flw.dto.iemr.FormSectionDTO;
import com.iemr.flw.dto.iemr.FormVersionDTO;
import com.iemr.flw.dto.iemr.OptionConditionDTO;
import com.iemr.flw.dto.iemr.QuestionOptionDTO;
import com.iemr.flw.dto.iemr.QuestionValidationDTO;
import com.iemr.flw.dto.iemr.SectionQuestionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for dynamic form definition — entity ↔ DTO conversion.
 */
@Mapper(componentModel = "spring")
public interface DynamicFormMapper {

    // ── Entity → DTO ──────────────────────────────────────────────────────────

    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    DynamicFormDTO toDto(DynamicForm entity);

    FormVersionDTO toDto(FormVersion entity);

    @Mapping(target = "questions", ignore = true)
    FormSectionDTO toDto(FormSection entity);

    @Mapping(target = "visibleByDefault", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "validations", ignore = true)
    SectionQuestionDTO toDto(SectionQuestion entity);

    @Mapping(target = "conditions", ignore = true)
    QuestionOptionDTO toDto(QuestionOption entity);

    @Mapping(source = "targetQuestion.questionId",   target = "targetQuestionId")
    @Mapping(source = "targetQuestion.questionUuid", target = "targetQuestionUuid")
    @Mapping(source = "targetSection.sectionId",     target = "targetSectionId")
    @Mapping(source = "targetSection.sectionUuid",   target = "targetSectionUuid")
    OptionConditionDTO toDto(OptionCondition entity);

    QuestionValidationDTO toDto(QuestionValidation entity);

    // ── DTO → Entity ──────────────────────────────────────────────────────────

    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DynamicForm toEntity(DynamicFormDTO dto);

    @Mapping(target = "dynamicForm", ignore = true)
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FormVersion toEntity(FormVersionDTO dto);

    @Mapping(target = "questions", ignore = true)
    FormSection toEntity(FormSectionDTO dto);

    @Mapping(target = "formSection", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "validations", ignore = true)
    SectionQuestion toEntity(SectionQuestionDTO dto);

    @Mapping(target = "sectionQuestion", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    QuestionOption toEntity(QuestionOptionDTO dto);

    @Mapping(target = "questionOption", ignore = true)
    @Mapping(target = "targetQuestion", ignore = true)
    @Mapping(target = "targetSection", ignore = true)
    OptionCondition toEntity(OptionConditionDTO dto);

    @Mapping(target = "sectionQuestion", ignore = true)
    QuestionValidation toEntity(QuestionValidationDTO dto);
}
