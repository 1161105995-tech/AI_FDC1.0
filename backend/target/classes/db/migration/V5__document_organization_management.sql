CREATE TABLE IF NOT EXISTS md_document_organization (
    id BIGSERIAL PRIMARY KEY,
    document_organization_code VARCHAR(64) NOT NULL,
    country_code VARCHAR(32) NOT NULL,
    city_code VARCHAR(64),
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_document_organization_code_active
    ON md_document_organization (document_organization_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_md_document_organization_country
    ON md_document_organization (country_code);

CREATE INDEX IF NOT EXISTS idx_md_document_organization_city
    ON md_document_organization (city_code);

CREATE TABLE IF NOT EXISTS md_document_organization_city (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(32) NOT NULL,
    city_code VARCHAR(64) NOT NULL,
    city_name VARCHAR(128) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 1,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_document_org_city_active
    ON md_document_organization_city (country_code, city_code)
    WHERE delete_flag = 'N';

INSERT INTO md_document_organization_city (
    country_code, city_code, city_name, sort_order, enabled_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
) VALUES
('CN', 'BEIJING', '北京', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('CN', 'SHANGHAI', '上海', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('CN', 'SHENZHEN', '深圳', 3, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('US', 'NEW_YORK', '纽约', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('US', 'SAN_FRANCISCO', '旧金山', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('SG', 'SINGAPORE', '新加坡', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DE', 'FRANKFURT', '法兰克福', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('AE', 'DUBAI', '迪拜', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
