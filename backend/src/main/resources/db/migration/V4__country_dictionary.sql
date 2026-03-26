CREATE TABLE IF NOT EXISTS md_country (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(32) NOT NULL,
    country_name VARCHAR(128) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 1,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_md_country_active
    ON md_country (country_code)
    WHERE delete_flag = 'N';

INSERT INTO md_country (
    country_code, country_name, sort_order, enabled_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
) VALUES
('CN', '中国', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('US', '美国', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('SG', '新加坡', 3, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DE', '德国', 4, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('AE', '阿联酋', 5, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

