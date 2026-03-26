CREATE TABLE IF NOT EXISTS md_company_project (
    id BIGSERIAL PRIMARY KEY,
    company_project_code VARCHAR(64) NOT NULL,
    company_project_name VARCHAR(128) NOT NULL,
    country_code VARCHAR(32) NOT NULL,
    management_area VARCHAR(128) NOT NULL,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_code_active
    ON md_company_project (company_project_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_md_company_project_country
    ON md_company_project (country_code);

CREATE TABLE IF NOT EXISTS md_company_project_line (
    id BIGSERIAL PRIMARY KEY,
    company_project_code VARCHAR(64) NOT NULL,
    line_no INTEGER NOT NULL,
    org_category VARCHAR(64) NOT NULL,
    organization_code VARCHAR(64) NOT NULL,
    organization_name VARCHAR(128) NOT NULL,
    valid_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_line_code_active
    ON md_company_project_line (company_project_code, organization_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_md_company_project_line_header
    ON md_company_project_line (company_project_code, line_no);

CREATE TABLE IF NOT EXISTS md_company_project_org_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(64) NOT NULL,
    category_name VARCHAR(128) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 1,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_org_category_active
    ON md_company_project_org_category (category_code)
    WHERE delete_flag = 'N';

INSERT INTO md_company_project_org_category (
    category_code, category_name, sort_order, enabled_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
) VALUES
('STANDARD_COMPANY', '标准公司', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('EXTERNAL_COMPANY', '外部公司', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('OTHER', '其他', 3, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

