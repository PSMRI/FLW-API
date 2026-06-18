-- Dynamic Form module schema for db_iemr
-- Run against your local MySQL: mysql -u root -p db_iemr < dynamic_form_schema.sql

USE db_iemr;

CREATE TABLE IF NOT EXISTS t_dynamic_form (
    formId BIGINT NOT NULL AUTO_INCREMENT,
    formUuid VARCHAR(100) NOT NULL,
    formName VARCHAR(255) NOT NULL,
    formType VARCHAR(50) NOT NULL,
    isActive BIT NOT NULL DEFAULT 1,
    follow_up_delay_days INT NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    PRIMARY KEY (formId),
    UNIQUE KEY uk_dynamic_form_uuid (formUuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_form_version (
    versionId BIGINT NOT NULL AUTO_INCREMENT,
    formId BIGINT NOT NULL,
    versionNumber INT NOT NULL,
    isLatest BIT NOT NULL DEFAULT 1,
    createdAt DATETIME NOT NULL,
    createdBy VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    notes TEXT NULL,
    PRIMARY KEY (versionId),
    KEY idx_form_version_formId (formId),
    CONSTRAINT fk_form_version_form FOREIGN KEY (formId) REFERENCES t_dynamic_form (formId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_form_section (
    sectionId BIGINT NOT NULL AUTO_INCREMENT,
    version_id BIGINT NOT NULL,
    sectionUuid VARCHAR(100) NOT NULL,
    sectionName VARCHAR(255) NOT NULL,
    sectionPhase VARCHAR(20) NOT NULL,
    isRequired BIT NOT NULL DEFAULT 1,
    displayOrder INT NOT NULL,
    hasSubmitButton BIT NOT NULL DEFAULT 0,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (sectionId),
    KEY idx_form_section_version (version_id),
    CONSTRAINT fk_form_section_version FOREIGN KEY (version_id) REFERENCES t_form_version (versionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_section_question (
    questionId BIGINT NOT NULL AUTO_INCREMENT,
    sectionId BIGINT NOT NULL,
    questionUuid VARCHAR(100) NOT NULL,
    questionText TEXT NOT NULL,
    questionType VARCHAR(20) NOT NULL,
    isMandatory BIT NOT NULL DEFAULT 1,
    displayOrder INT NOT NULL,
    maxLength INT NULL,
    defaultValue VARCHAR(500) NULL,
    containsPii BIT NOT NULL DEFAULT 0,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (questionId),
    KEY idx_section_question_sectionId (sectionId),
    CONSTRAINT fk_section_question_section FOREIGN KEY (sectionId) REFERENCES t_form_section (sectionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_question_option (
    optionId BIGINT NOT NULL AUTO_INCREMENT,
    questionId BIGINT NOT NULL,
    optionLabel VARCHAR(255) NOT NULL,
    optionValue VARCHAR(100) NOT NULL,
    displayOrder INT NOT NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (optionId),
    KEY idx_question_option_questionId (questionId),
    CONSTRAINT fk_question_option_question FOREIGN KEY (questionId) REFERENCES t_section_question (questionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_question_validation (
    validationId BIGINT NOT NULL AUTO_INCREMENT,
    questionId BIGINT NOT NULL,
    validationType VARCHAR(30) NOT NULL,
    validationParam VARCHAR(255) NULL,
    errorMessage VARCHAR(500) NOT NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (validationId),
    KEY idx_question_validation_questionId (questionId),
    CONSTRAINT fk_question_validation_question FOREIGN KEY (questionId) REFERENCES t_section_question (questionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_option_condition (
    conditionId BIGINT NOT NULL AUTO_INCREMENT,
    optionId BIGINT NOT NULL,
    actionType VARCHAR(40) NOT NULL,
    targetQuestionId BIGINT NULL,
    targetSectionId BIGINT NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (conditionId),
    KEY idx_option_condition_optionId (optionId),
    CONSTRAINT fk_option_condition_option FOREIGN KEY (optionId) REFERENCES t_question_option (optionId),
    CONSTRAINT fk_option_condition_target_question FOREIGN KEY (targetQuestionId) REFERENCES t_section_question (questionId),
    CONSTRAINT fk_option_condition_target_section FOREIGN KEY (targetSectionId) REFERENCES t_form_section (sectionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_form_response (
    responseId BIGINT NOT NULL AUTO_INCREMENT,
    beneficiaryId BIGINT NOT NULL,
    formId BIGINT NOT NULL,
    versionId BIGINT NOT NULL,
    officerId BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    submittedAt DATETIME NULL,
    completedAt DATETIME NULL,
    last_follow_up_at DATETIME NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    PRIMARY KEY (responseId),
    KEY idx_form_response_ben_form (beneficiaryId, formId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_section_response (
    sectionResponseId BIGINT NOT NULL AUTO_INCREMENT,
    responseId BIGINT NOT NULL,
    sectionId BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    savedAt DATETIME NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (sectionResponseId),
    KEY idx_section_response_responseId (responseId),
    CONSTRAINT fk_section_response_response FOREIGN KEY (responseId) REFERENCES t_form_response (responseId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_question_response (
    questionResponseId BIGINT NOT NULL AUTO_INCREMENT,
    sectionResponseId BIGINT NOT NULL,
    questionId BIGINT NOT NULL,
    optionId BIGINT NULL,
    answerText TEXT NULL,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (questionResponseId),
    KEY idx_question_response_sectionResponseId (sectionResponseId),
    CONSTRAINT fk_question_response_section_response FOREIGN KEY (sectionResponseId) REFERENCES t_section_response (sectionResponseId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─────────────────────────────────────────────────────────────
-- Sample seed data: one form, one version, one section, two questions
-- ─────────────────────────────────────────────────────────────

INSERT INTO t_dynamic_form (formUuid, formName, formType, isActive, created_by, updated_by, createdAt, updatedAt)
VALUES ('11111111-1111-1111-1111-111111111111', 'TB Counselling Form', 'COUNSELLING', 1, 'system', 'system', NOW(), NOW());

INSERT INTO t_form_version (formId, versionNumber, isLatest, createdAt, createdBy, updated_by, notes)
VALUES (LAST_INSERT_ID(), 1, 1, NOW(), 'system', 'system', 'initial version');

INSERT INTO t_form_section (version_id, sectionUuid, sectionName, sectionPhase, isRequired, displayOrder, hasSubmitButton, created_by, updated_by)
VALUES (LAST_INSERT_ID(), '22222222-2222-2222-2222-222222222222', 'Disease Awareness', 'PRE_SUBMIT', 1, 1, 1, 'system', 'system');

INSERT INTO t_section_question (sectionId, questionUuid, questionText, questionType, isMandatory, displayOrder, containsPii, created_by, updated_by)
VALUES (LAST_INSERT_ID(), '33333333-3333-3333-3333-333333333333', 'Has the beneficiary heard of TB before?', 'RADIO', 1, 1, 0, 'system', 'system');

INSERT INTO t_question_option (questionId, optionLabel, optionValue, displayOrder, created_by, updated_by)
VALUES
(LAST_INSERT_ID(), 'Yes', 'YES', 1, 'system', 'system'),
(LAST_INSERT_ID(), 'No', 'NO', 2, 'system', 'system');
