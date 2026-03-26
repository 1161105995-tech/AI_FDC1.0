CREATE TABLE IF NOT EXISTS md_security_level (
    id BIGSERIAL PRIMARY KEY,
    security_level_code VARCHAR(32) NOT NULL,
    security_level_name VARCHAR(128) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 1,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_security_level_code_active
    ON md_security_level (security_level_code)
    WHERE delete_flag = 'N';

INSERT INTO md_security_level (
    security_level_code, security_level_name, sort_order, enabled_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
) VALUES
('PUBLIC', '公开', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('INTERNAL', '内部', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('SECRET', '秘密', 3, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('CONFIDENTIAL', '机密', 4, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS md_archive_flow_rule (
    id BIGSERIAL PRIMARY KEY,
    company_project_code VARCHAR(64) NOT NULL,
    document_type_code VARCHAR(64) NOT NULL,
    custom_rule VARCHAR(500),
    archive_destination VARCHAR(64),
    document_organization_code VARCHAR(64) NOT NULL,
    retention_period_years INTEGER NOT NULL,
    security_level_code VARCHAR(32) NOT NULL,
    external_display_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_archive_flow_rule_cp_active
    ON md_archive_flow_rule (company_project_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_md_archive_flow_rule_type
    ON md_archive_flow_rule (document_type_code);

CREATE INDEX IF NOT EXISTS idx_md_archive_flow_rule_org
    ON md_archive_flow_rule (document_organization_code);

CREATE INDEX IF NOT EXISTS idx_md_archive_flow_rule_security
    ON md_archive_flow_rule (security_level_code);

CREATE INDEX IF NOT EXISTS idx_md_archive_flow_rule_enabled
    ON md_archive_flow_rule (enabled_flag);
