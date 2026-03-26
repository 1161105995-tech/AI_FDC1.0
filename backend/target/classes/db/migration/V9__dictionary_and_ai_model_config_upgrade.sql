CREATE TABLE IF NOT EXISTS md_dict_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(64) NOT NULL UNIQUE,
    category_name VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_md_dict_category_enabled CHECK (enabled_flag IN ('Y','N')),
    CONSTRAINT ck_md_dict_category_delete CHECK (delete_flag IN ('Y','N'))
);

CREATE TABLE IF NOT EXISTS md_dict_item (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(64) NOT NULL,
    item_code VARCHAR(64) NOT NULL,
    item_name VARCHAR(128) NOT NULL,
    item_value VARCHAR(128),
    sort_order INTEGER NOT NULL DEFAULT 1,
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_md_dict_item_category_code_item_code UNIQUE (category_code, item_code),
    CONSTRAINT ck_md_dict_item_enabled CHECK (enabled_flag IN ('Y','N')),
    CONSTRAINT ck_md_dict_item_delete CHECK (delete_flag IN ('Y','N'))
);

CREATE INDEX IF NOT EXISTS idx_md_dict_item_category ON md_dict_item(category_code, enabled_flag, delete_flag, sort_order);

INSERT INTO md_dict_category (category_code, category_name, description)
SELECT 'ARCHIVE_CARRIER_TYPE', '载体类型', '创建档案时使用的载体类型字典'
WHERE NOT EXISTS (SELECT 1 FROM md_dict_category WHERE category_code = 'ARCHIVE_CARRIER_TYPE');

INSERT INTO md_dict_category (category_code, category_name, description)
SELECT 'ARCHIVE_ATTACHMENT_TYPE', '附件类型', '电子附件和扫描件的类型字典'
WHERE NOT EXISTS (SELECT 1 FROM md_dict_category WHERE category_code = 'ARCHIVE_ATTACHMENT_TYPE');

INSERT INTO md_dict_category (category_code, category_name, description)
SELECT 'ARCHIVE_TYPE', '档案类型', '档案主表归档类型字典'
WHERE NOT EXISTS (SELECT 1 FROM md_dict_category WHERE category_code = 'ARCHIVE_TYPE');

INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_CARRIER_TYPE', 'ELECTRONIC', '电子件', 'ELECTRONIC', 1
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_CARRIER_TYPE' AND item_code = 'ELECTRONIC');
INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_CARRIER_TYPE', 'PAPER', '纸质件', 'PAPER', 2
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_CARRIER_TYPE' AND item_code = 'PAPER');
INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_CARRIER_TYPE', 'HYBRID', '电子件+纸质件', 'HYBRID', 3
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_CARRIER_TYPE' AND item_code = 'HYBRID');

INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_ATTACHMENT_TYPE', 'ACCOUNTING_VOUCHER', '记账凭证', 'ACCOUNTING_VOUCHER', 1
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_ATTACHMENT_TYPE' AND item_code = 'ACCOUNTING_VOUCHER');
INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_ATTACHMENT_TYPE', 'SUPPORTING_ATTACHMENT', '支撑附件', 'SUPPORTING_ATTACHMENT', 2
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_ATTACHMENT_TYPE' AND item_code = 'SUPPORTING_ATTACHMENT');

INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_TYPE', 'GENERAL', '通用档案', 'GENERAL', 1
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_TYPE' AND item_code = 'GENERAL');
INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_TYPE', 'FINANCIAL', '财务档案', 'FINANCIAL', 2
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_TYPE' AND item_code = 'FINANCIAL');
INSERT INTO md_dict_item (category_code, item_code, item_name, item_value, sort_order)
SELECT 'ARCHIVE_TYPE', 'PROJECT', '项目档案', 'PROJECT', 3
WHERE NOT EXISTS (SELECT 1 FROM md_dict_item WHERE category_code = 'ARCHIVE_TYPE' AND item_code = 'PROJECT');

ALTER TABLE ai_model_config
    ADD COLUMN IF NOT EXISTS official_result_count INTEGER NOT NULL DEFAULT 10,
    ADD COLUMN IF NOT EXISTS official_score_threshold NUMERIC(10,4) NOT NULL DEFAULT 0.72,
    ADD COLUMN IF NOT EXISTS related_result_count INTEGER NOT NULL DEFAULT 20,
    ADD COLUMN IF NOT EXISTS related_score_threshold NUMERIC(10,4) NOT NULL DEFAULT 0.45;
