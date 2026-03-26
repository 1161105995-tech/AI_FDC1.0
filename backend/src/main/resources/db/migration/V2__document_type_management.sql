CREATE TABLE IF NOT EXISTS doc_document_type (
    id BIGSERIAL PRIMARY KEY,
    type_code VARCHAR(64) NOT NULL,
    type_name VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    enabled_flag VARCHAR(1) NOT NULL DEFAULT 'Y',
    parent_code VARCHAR(64),
    level_num INTEGER NOT NULL,
    ancestor_path VARCHAR(512),
    sort_order INTEGER NOT NULL DEFAULT 1,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 0,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_document_type_code_active
    ON doc_document_type (type_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_doc_document_type_parent_code
    ON doc_document_type (parent_code);

CREATE TABLE IF NOT EXISTS com_operation_audit (
    id BIGSERIAL PRIMARY KEY,
    module_code VARCHAR(64) NOT NULL,
    module_name VARCHAR(128) NOT NULL,
    business_type VARCHAR(64) NOT NULL,
    business_key VARCHAR(128) NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    operation_summary VARCHAR(255) NOT NULL,
    before_snapshot TEXT,
    after_snapshot TEXT,
    operator_id BIGINT NOT NULL DEFAULT 0,
    operator_name VARCHAR(64) NOT NULL DEFAULT 'system',
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_com_operation_audit_module_time
    ON com_operation_audit (module_code, operation_time DESC);

INSERT INTO doc_document_type (
    type_code, type_name, description, enabled_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
) VALUES
('DOC', '文档', '文档类型根节点', 'Y', NULL, 1, NULL, 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DOC_FIN', '财务文档', '财务类文档目录', 'Y', 'DOC', 2, 'DOC', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DOC_FIN_VCH', '会计凭证', '会计凭证分类', 'Y', 'DOC_FIN', 3, 'DOC/DOC_FIN', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DOC_FIN_VCH_PAY', '付款凭证', '付款业务凭证', 'Y', 'DOC_FIN_VCH', 4, 'DOC/DOC_FIN/DOC_FIN_VCH', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DOC_FIN_VCH_PAY_DOM', '境内付款凭证', '五级示例节点', 'Y', 'DOC_FIN_VCH_PAY', 5, 'DOC/DOC_FIN/DOC_FIN_VCH/DOC_FIN_VCH_PAY', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP),
('DOC_CON', '合同文档', '合同类文档目录', 'Y', 'DOC', 2, 'DOC', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
