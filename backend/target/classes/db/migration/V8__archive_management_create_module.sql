CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE md_archive_flow_rule
    ADD COLUMN IF NOT EXISTS custom_rule_normalized VARCHAR(128) DEFAULT '' NOT NULL,
    ADD COLUMN IF NOT EXISTS archive_destination_normalized VARCHAR(64) DEFAULT '' NOT NULL;

UPDATE md_archive_flow_rule
SET custom_rule_normalized = COALESCE(NULLIF(BTRIM(custom_rule), ''), ''),
    archive_destination_normalized = COALESCE(NULLIF(BTRIM(archive_destination), ''), '')
WHERE TRUE;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_md_archive_flow_rule_company_project'
    ) THEN
        ALTER TABLE md_archive_flow_rule DROP CONSTRAINT uk_md_archive_flow_rule_company_project;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_md_archive_flow_rule_business'
    ) THEN
        ALTER TABLE md_archive_flow_rule
            ADD CONSTRAINT uk_md_archive_flow_rule_business UNIQUE (
                company_project_code,
                document_type_code,
                custom_rule_normalized,
                archive_destination_normalized
            );
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS arc_ext_field_config (
    field_id BIGSERIAL PRIMARY KEY,
    field_code VARCHAR(64) NOT NULL UNIQUE,
    document_type_code VARCHAR(64) NOT NULL,
    field_name VARCHAR(128) NOT NULL,
    field_type VARCHAR(32) NOT NULL,
    dict_category_code VARCHAR(64),
    required_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    form_sort_order INTEGER NOT NULL DEFAULT 1,
    query_enabled_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    query_sort_order INTEGER NOT NULL DEFAULT 1,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_ext_field_config_type CHECK (field_type IN ('TEXT', 'DICT')),
    CONSTRAINT ck_arc_ext_field_config_required CHECK (required_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_ext_field_config_enabled CHECK (enabled_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_ext_field_config_query CHECK (query_enabled_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_ext_field_config_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_ext_field_config_type
    ON arc_ext_field_config (document_type_code, enabled_flag, delete_flag, form_sort_order);

CREATE INDEX IF NOT EXISTS idx_arc_ext_field_config_query
    ON arc_ext_field_config (document_type_code, query_enabled_flag, delete_flag, query_sort_order);

CREATE TABLE IF NOT EXISTS arc_archive_create_session (
    session_id BIGSERIAL PRIMARY KEY,
    session_code VARCHAR(64) NOT NULL UNIQUE,
    create_mode VARCHAR(16) NOT NULL,
    session_status VARCHAR(32) NOT NULL,
    document_type_code_guess VARCHAR(64),
    carrier_type_code_guess VARCHAR(32),
    parse_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    ai_summary_snapshot TEXT,
    owner_user_id BIGINT NOT NULL DEFAULT 1,
    expire_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_archive_create_session_mode CHECK (create_mode IN ('AUTO', 'MANUAL')),
    CONSTRAINT ck_arc_archive_create_session_status CHECK (session_status IN ('UPLOADING', 'PARSING', 'READY', 'SAVED', 'EXPIRED')),
    CONSTRAINT ck_arc_archive_create_session_parse CHECK (parse_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED'))
);

CREATE TABLE IF NOT EXISTS arc_archive (
    archive_id BIGSERIAL PRIMARY KEY,
    archive_code VARCHAR(64) NOT NULL UNIQUE,
    archive_filing_code VARCHAR(64) NOT NULL UNIQUE,
    create_mode VARCHAR(16) NOT NULL,
    archive_status VARCHAR(32) NOT NULL,
    document_type_code VARCHAR(64) NOT NULL,
    company_project_code VARCHAR(64) NOT NULL,
    begin_period VARCHAR(7) NOT NULL,
    end_period VARCHAR(7) NOT NULL,
    business_code VARCHAR(128),
    document_name VARCHAR(256) NOT NULL,
    duty_person VARCHAR(64) NOT NULL,
    duty_department VARCHAR(128) NOT NULL,
    document_date DATE NOT NULL,
    security_level_code VARCHAR(32) NOT NULL,
    source_system VARCHAR(128),
    archive_destination VARCHAR(64),
    origin_place VARCHAR(128),
    carrier_type_code VARCHAR(32) NOT NULL,
    remark VARCHAR(1000),
    ai_archive_summary TEXT,
    ai_parse_confidence NUMERIC(5,2),
    document_organization_code VARCHAR(64) NOT NULL,
    retention_period_years INTEGER NOT NULL,
    archive_type_code VARCHAR(64) NOT NULL,
    country_code VARCHAR(32),
    parse_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    vector_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    qa_index_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    four_nature_check_status VARCHAR(32),
    four_nature_report_id BIGINT,
    session_id BIGINT,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_archive_mode CHECK (create_mode IN ('AUTO', 'MANUAL')),
    CONSTRAINT ck_arc_archive_status CHECK (archive_status IN ('DRAFT', 'CREATED', 'TRANSFERRED', 'BOUND', 'STORED')),
    CONSTRAINT ck_arc_archive_carrier CHECK (carrier_type_code IN ('ELECTRONIC', 'PAPER', 'HYBRID')),
    CONSTRAINT ck_arc_archive_parse CHECK (parse_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_vector CHECK (vector_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_qa CHECK (qa_index_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_archive_doc_type
    ON arc_archive (document_type_code, delete_flag);
CREATE INDEX IF NOT EXISTS idx_arc_archive_company_project
    ON arc_archive (company_project_code, delete_flag);
CREATE INDEX IF NOT EXISTS idx_arc_archive_document_date
    ON arc_archive (document_date, delete_flag);
CREATE INDEX IF NOT EXISTS idx_arc_archive_status
    ON arc_archive (archive_status, delete_flag);

CREATE TABLE IF NOT EXISTS arc_archive_ext_value (
    value_id BIGSERIAL PRIMARY KEY,
    archive_id BIGINT NOT NULL REFERENCES arc_archive (archive_id),
    field_code VARCHAR(64) NOT NULL,
    field_name_snapshot VARCHAR(128) NOT NULL,
    field_type VARCHAR(32) NOT NULL,
    dict_category_code VARCHAR(64),
    dict_item_code VARCHAR(64),
    dict_item_name_snapshot VARCHAR(128),
    text_value TEXT,
    value_source VARCHAR(16) NOT NULL DEFAULT 'MANUAL',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_arc_archive_ext_value UNIQUE (archive_id, field_code),
    CONSTRAINT ck_arc_archive_ext_value_field_type CHECK (field_type IN ('TEXT', 'DICT')),
    CONSTRAINT ck_arc_archive_ext_value_source CHECK (value_source IN ('DEFAULT', 'AI', 'MANUAL'))
);

CREATE INDEX IF NOT EXISTS idx_arc_archive_ext_value_archive
    ON arc_archive_ext_value (archive_id);

CREATE TABLE IF NOT EXISTS arc_archive_attachment (
    attachment_id BIGSERIAL PRIMARY KEY,
    archive_id BIGINT REFERENCES arc_archive (archive_id),
    session_id BIGINT REFERENCES arc_archive_create_session (session_id),
    attachment_role VARCHAR(32) NOT NULL,
    attachment_type_code VARCHAR(64),
    attachment_seq INTEGER NOT NULL DEFAULT 1,
    version_no INTEGER NOT NULL DEFAULT 1,
    file_name VARCHAR(255) NOT NULL,
    file_ext VARCHAR(32),
    mime_type VARCHAR(128),
    file_size BIGINT,
    storage_path VARCHAR(1000) NOT NULL,
    storage_key VARCHAR(256) NOT NULL,
    file_hash VARCHAR(128),
    remark VARCHAR(1000),
    ai_summary TEXT,
    ocr_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    parse_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    vector_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    active_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_archive_attachment_role CHECK (attachment_role IN ('ELECTRONIC', 'PAPER_SCAN')),
    CONSTRAINT ck_arc_archive_attachment_ocr CHECK (ocr_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_attachment_parse CHECK (parse_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_attachment_vector CHECK (vector_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED')),
    CONSTRAINT ck_arc_archive_attachment_active CHECK (active_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_archive_attachment_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_archive_attachment_archive
    ON arc_archive_attachment (archive_id, delete_flag);
CREATE INDEX IF NOT EXISTS idx_arc_archive_attachment_session
    ON arc_archive_attachment (session_id, delete_flag);

CREATE TABLE IF NOT EXISTS arc_archive_paper (
    paper_id BIGSERIAL PRIMARY KEY,
    archive_id BIGINT NOT NULL UNIQUE REFERENCES arc_archive (archive_id),
    planned_copy_count INTEGER,
    actual_copy_count INTEGER,
    remark VARCHAR(1000),
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS arc_archive_content (
    content_id BIGSERIAL PRIMARY KEY,
    archive_id BIGINT NOT NULL REFERENCES arc_archive (archive_id),
    attachment_id BIGINT NOT NULL REFERENCES arc_archive_attachment (attachment_id),
    content_version INTEGER NOT NULL,
    full_text TEXT,
    text_length INTEGER,
    parse_time TIMESTAMP,
    ocr_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_arc_archive_content_version UNIQUE (attachment_id, content_version),
    CONSTRAINT ck_arc_archive_content_ocr CHECK (ocr_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_archive_content_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE TABLE IF NOT EXISTS arc_archive_content_chunk (
    chunk_id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL REFERENCES arc_archive_content (content_id),
    archive_id BIGINT NOT NULL REFERENCES arc_archive (archive_id),
    attachment_id BIGINT NOT NULL REFERENCES arc_archive_attachment (attachment_id),
    chunk_no INTEGER NOT NULL,
    chunk_text TEXT NOT NULL,
    token_count INTEGER,
    page_no INTEGER,
    position_start INTEGER,
    position_end INTEGER,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_arc_archive_content_chunk UNIQUE (content_id, chunk_no),
    CONSTRAINT ck_arc_archive_content_chunk_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE TABLE IF NOT EXISTS arc_archive_chunk_vector (
    vector_id BIGSERIAL PRIMARY KEY,
    chunk_id BIGINT NOT NULL REFERENCES arc_archive_content_chunk (chunk_id),
    archive_id BIGINT NOT NULL REFERENCES arc_archive (archive_id),
    attachment_id BIGINT NOT NULL REFERENCES arc_archive_attachment (attachment_id),
    embedding_model_code VARCHAR(64) NOT NULL,
    vector_value vector(256) NOT NULL,
    vector_dimension INTEGER NOT NULL DEFAULT 256,
    content_version INTEGER NOT NULL,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_arc_archive_chunk_vector UNIQUE (chunk_id, embedding_model_code, content_version),
    CONSTRAINT ck_arc_archive_chunk_vector_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_archive_chunk_vector_archive
    ON arc_archive_chunk_vector (archive_id, embedding_model_code);

CREATE TABLE IF NOT EXISTS arc_archive_ai_task (
    task_id BIGSERIAL PRIMARY KEY,
    archive_id BIGINT,
    attachment_id BIGINT,
    session_id BIGINT,
    task_type VARCHAR(32) NOT NULL,
    task_status VARCHAR(32) NOT NULL,
    model_code VARCHAR(64),
    request_payload JSONB,
    response_payload JSONB,
    error_msg TEXT,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_archive_ai_task_type CHECK (task_type IN ('META_EXTRACT', 'OCR', 'SUMMARY', 'VECTORIZE', 'QA_INDEX', 'FOUR_NATURE')),
    CONSTRAINT ck_arc_archive_ai_task_status CHECK (task_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED'))
);

CREATE TABLE IF NOT EXISTS ai_model_config (
    model_config_id BIGSERIAL PRIMARY KEY,
    model_code VARCHAR(64) NOT NULL UNIQUE,
    model_name VARCHAR(128) NOT NULL,
    model_type VARCHAR(64) NOT NULL,
    api_url VARCHAR(500) NOT NULL,
    api_key VARCHAR(500) NOT NULL,
    model_identifier VARCHAR(128) NOT NULL,
    prompt_template TEXT,
    embedding_dimension INTEGER NOT NULL DEFAULT 256,
    timeout_seconds INTEGER NOT NULL DEFAULT 30,
    top_k INTEGER NOT NULL DEFAULT 5,
    score_threshold NUMERIC(10,4),
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    remark VARCHAR(500),
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_ai_model_config_type CHECK (model_type IN ('CHAT', 'EMBEDDING', 'RERANK')),
    CONSTRAINT ck_ai_model_config_enabled CHECK (enabled_flag IN ('Y', 'N')),
    CONSTRAINT ck_ai_model_config_delete CHECK (delete_flag IN ('Y', 'N'))
);

INSERT INTO ai_model_config (
    model_code, model_name, model_type, api_url, api_key, model_identifier, prompt_template,
    embedding_dimension, timeout_seconds, top_k, score_threshold, enabled_flag, remark
)
SELECT
    'LOCAL_CHAT', 'Local Chat Summarizer', 'CHAT', 'local://archive-chat', 'N/A', 'local-chat',
    'Use retrieved archive content and structured fields to answer briefly and accurately.',
    256, 30, 5, 0.3, 'Y', 'Local fallback model configuration'
WHERE NOT EXISTS (SELECT 1 FROM ai_model_config WHERE model_code = 'LOCAL_CHAT');

INSERT INTO ai_model_config (
    model_code, model_name, model_type, api_url, api_key, model_identifier, prompt_template,
    embedding_dimension, timeout_seconds, top_k, score_threshold, enabled_flag, remark
)
SELECT
    'LOCAL_EMBEDDING', 'Local Embedding Model', 'EMBEDDING', 'local://archive-embedding', 'N/A', 'local-embedding',
    'Generate deterministic fallback embeddings for archive indexing.', 256, 30, 8, 0.2, 'Y', 'Local fallback embedding configuration'
WHERE NOT EXISTS (SELECT 1 FROM ai_model_config WHERE model_code = 'LOCAL_EMBEDDING');
