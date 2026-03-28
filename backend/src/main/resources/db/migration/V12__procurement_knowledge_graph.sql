ALTER TABLE arc_ext_field_config
    ADD COLUMN IF NOT EXISTS semantic_code VARCHAR(64);

CREATE INDEX IF NOT EXISTS idx_arc_ext_field_config_semantic
    ON arc_ext_field_config (semantic_code, document_type_code, delete_flag);

CREATE TABLE IF NOT EXISTS kg_matching_rule_version (
    version_id BIGSERIAL PRIMARY KEY,
    scenario_code VARCHAR(64) NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    primary_keys_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    auxiliary_keys_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    match_mode VARCHAR(32) NOT NULL DEFAULT 'EXACT',
    amount_tolerance NUMERIC(18, 2),
    time_window_days INTEGER,
    conflict_strategy VARCHAR(64) NOT NULL DEFAULT 'MARK_BROKEN',
    rule_version INTEGER NOT NULL,
    current_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_kg_matching_rule_current CHECK (current_flag IN ('Y', 'N')),
    CONSTRAINT ck_kg_matching_rule_enabled CHECK (enabled_flag IN ('Y', 'N'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_kg_matching_rule_scenario_version
    ON kg_matching_rule_version (scenario_code, rule_version);

CREATE UNIQUE INDEX IF NOT EXISTS uk_kg_matching_rule_scenario_current
    ON kg_matching_rule_version (scenario_code, current_flag)
    WHERE current_flag = 'Y';

CREATE TABLE IF NOT EXISTS kg_rebuild_task (
    task_id BIGSERIAL PRIMARY KEY,
    task_code VARCHAR(64) NOT NULL UNIQUE,
    trigger_type VARCHAR(32) NOT NULL,
    target_scope VARCHAR(32) NOT NULL,
    target_value VARCHAR(256),
    requested_rule_version INTEGER,
    task_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    summary TEXT,
    diff_json JSONB,
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kg_rebuild_task_scope
    ON kg_rebuild_task (target_scope, target_value, task_status);

CREATE TABLE IF NOT EXISTS kg_graph_node (
    node_id BIGSERIAL PRIMARY KEY,
    node_key VARCHAR(256) NOT NULL UNIQUE,
    node_type VARCHAR(64) NOT NULL,
    entity_key VARCHAR(256),
    entity_name VARCHAR(256),
    node_status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
    anchor_archive_id BIGINT,
    attributes_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kg_graph_node_type
    ON kg_graph_node (node_type, entity_key);

CREATE TABLE IF NOT EXISTS kg_graph_edge (
    edge_id BIGSERIAL PRIMARY KEY,
    edge_key VARCHAR(256) NOT NULL UNIQUE,
    edge_type VARCHAR(64) NOT NULL,
    from_node_key VARCHAR(256) NOT NULL,
    to_node_key VARCHAR(256) NOT NULL,
    edge_status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
    confidence NUMERIC(10, 4),
    source_archive_id BIGINT,
    source_field_code VARCHAR(64),
    source_semantic_code VARCHAR(64),
    match_rule_code VARCHAR(64),
    rule_version INTEGER,
    attributes_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kg_graph_edge_from
    ON kg_graph_edge (from_node_key, edge_type);

CREATE INDEX IF NOT EXISTS idx_kg_graph_edge_to
    ON kg_graph_edge (to_node_key, edge_type);

CREATE INDEX IF NOT EXISTS idx_kg_graph_edge_source_archive
    ON kg_graph_edge (source_archive_id);

CREATE TABLE IF NOT EXISTS kg_procurement_conversation (
    conversation_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    anchor_type VARCHAR(32),
    anchor_key VARCHAR(256),
    scope_mode VARCHAR(32) NOT NULL DEFAULT 'CURRENT_CHAIN',
    filters_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    context_summary TEXT,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_kg_procurement_conversation_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_kg_procurement_conversation_anchor
    ON kg_procurement_conversation (anchor_type, anchor_key, delete_flag);

CREATE TABLE IF NOT EXISTS kg_procurement_conversation_message (
    message_id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES kg_procurement_conversation (conversation_id),
    message_role VARCHAR(16) NOT NULL,
    question TEXT,
    answer TEXT,
    references_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    evidence_items_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    graph_snapshot_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    context_summary TEXT,
    suggested_followups_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kg_procurement_message_conversation
    ON kg_procurement_conversation_message (conversation_id, creation_date);

CREATE TABLE IF NOT EXISTS kg_procurement_context_snapshot (
    snapshot_id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES kg_procurement_conversation (conversation_id),
    message_id BIGINT REFERENCES kg_procurement_conversation_message (message_id),
    anchor_type VARCHAR(32),
    anchor_key VARCHAR(256),
    scope_mode VARCHAR(32) NOT NULL DEFAULT 'CURRENT_CHAIN',
    filters_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    node_keys_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    edge_keys_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    evidence_archive_ids_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    summary_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kg_procurement_context_conversation
    ON kg_procurement_context_snapshot (conversation_id, created_at);

INSERT INTO kg_matching_rule_version (
    scenario_code, rule_name, primary_keys_json, auxiliary_keys_json, match_mode,
    amount_tolerance, time_window_days, conflict_strategy, rule_version, current_flag, enabled_flag
)
SELECT
    'CONTRACT_TO_INVOICE',
    '合同关联发票',
    '["CONTRACT_NO"]'::jsonb,
    '["SUPPLIER_NAME"]'::jsonb,
    'EXACT',
    0,
    3650,
    'MARK_BROKEN',
    1,
    'Y',
    'Y'
WHERE NOT EXISTS (
    SELECT 1 FROM kg_matching_rule_version WHERE scenario_code = 'CONTRACT_TO_INVOICE' AND current_flag = 'Y'
);

INSERT INTO kg_matching_rule_version (
    scenario_code, rule_name, primary_keys_json, auxiliary_keys_json, match_mode,
    amount_tolerance, time_window_days, conflict_strategy, rule_version, current_flag, enabled_flag
)
SELECT
    'INVOICE_TO_PAYMENT',
    '发票关联付款',
    '["INVOICE_NO"]'::jsonb,
    '["CONTRACT_NO","SUPPLIER_NAME"]'::jsonb,
    'EXACT',
    0,
    3650,
    'CANDIDATE_ONLY_ON_MISSING_PRIMARY',
    1,
    'Y',
    'Y'
WHERE NOT EXISTS (
    SELECT 1 FROM kg_matching_rule_version WHERE scenario_code = 'INVOICE_TO_PAYMENT' AND current_flag = 'Y'
);

INSERT INTO kg_matching_rule_version (
    scenario_code, rule_name, primary_keys_json, auxiliary_keys_json, match_mode,
    amount_tolerance, time_window_days, conflict_strategy, rule_version, current_flag, enabled_flag
)
SELECT
    'SUPPLIER_TO_ACCOUNT',
    '供应商关联账号',
    '["SUPPLIER_NAME","PAYEE_BANK_ACCOUNT"]'::jsonb,
    '["PAYEE_NAME","PAYEE_BANK_NAME"]'::jsonb,
    'NORMALIZED',
    0,
    1095,
    'MARK_BROKEN',
    1,
    'Y',
    'Y'
WHERE NOT EXISTS (
    SELECT 1 FROM kg_matching_rule_version WHERE scenario_code = 'SUPPLIER_TO_ACCOUNT' AND current_flag = 'Y'
);
