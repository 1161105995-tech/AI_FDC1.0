ALTER TABLE arc_archive
    ADD COLUMN IF NOT EXISTS bind_volume_code VARCHAR(64),
    ADD COLUMN IF NOT EXISTS current_warehouse_code VARCHAR(64),
    ADD COLUMN IF NOT EXISTS current_location_code VARCHAR(64);

CREATE INDEX IF NOT EXISTS idx_arc_archive_bind_volume
    ON arc_archive (bind_volume_code, delete_flag);

CREATE TABLE IF NOT EXISTS arc_bind_batch (
    bind_batch_id BIGSERIAL PRIMARY KEY,
    bind_batch_code VARCHAR(64) NOT NULL UNIQUE,
    bind_mode VARCHAR(32) NOT NULL,
    source_type VARCHAR(32) NOT NULL DEFAULT 'ARCHIVE',
    bind_status VARCHAR(32) NOT NULL,
    bind_remark VARCHAR(1000),
    guided_storage_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    volume_count INTEGER NOT NULL DEFAULT 0,
    archive_count INTEGER NOT NULL DEFAULT 0,
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_bind_batch_mode CHECK (bind_mode IN ('BUSINESS_CODE', 'PERIOD', 'MANUAL')),
    CONSTRAINT ck_arc_bind_batch_status CHECK (bind_status IN ('DRAFT', 'BOUND', 'STORED')),
    CONSTRAINT ck_arc_bind_batch_guided CHECK (guided_storage_flag IN ('Y', 'N')),
    CONSTRAINT ck_arc_bind_batch_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_bind_batch_status
    ON arc_bind_batch (bind_status, delete_flag, creation_date DESC);

CREATE TABLE IF NOT EXISTS arc_bind_volume (
    volume_id BIGSERIAL PRIMARY KEY,
    bind_batch_id BIGINT NOT NULL REFERENCES arc_bind_batch (bind_batch_id),
    bind_volume_code VARCHAR(64) NOT NULL UNIQUE,
    volume_title VARCHAR(255) NOT NULL,
    bind_rule_key VARCHAR(255),
    carrier_type_code VARCHAR(32) NOT NULL,
    archive_count INTEGER NOT NULL DEFAULT 0,
    total_page_count INTEGER NOT NULL DEFAULT 0,
    total_copy_count INTEGER NOT NULL DEFAULT 0,
    bind_status VARCHAR(32) NOT NULL,
    remark VARCHAR(1000),
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_bind_volume_status CHECK (bind_status IN ('DRAFT', 'BOUND', 'STORED')),
    CONSTRAINT ck_arc_bind_volume_carrier CHECK (carrier_type_code IN ('ELECTRONIC', 'PAPER', 'HYBRID')),
    CONSTRAINT ck_arc_bind_volume_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_bind_volume_batch
    ON arc_bind_volume (bind_batch_id, delete_flag);

CREATE TABLE IF NOT EXISTS arc_bind_volume_item (
    item_id BIGSERIAL PRIMARY KEY,
    volume_id BIGINT NOT NULL REFERENCES arc_bind_volume (volume_id),
    archive_id BIGINT NOT NULL REFERENCES arc_archive (archive_id),
    sort_no INTEGER NOT NULL DEFAULT 1,
    primary_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    bind_reason VARCHAR(255),
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_arc_bind_volume_item_archive UNIQUE (archive_id),
    CONSTRAINT ck_arc_bind_volume_item_primary CHECK (primary_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_arc_bind_volume_item_volume
    ON arc_bind_volume_item (volume_id, sort_no);

CREATE TABLE IF NOT EXISTS arc_storage_batch (
    storage_batch_id BIGSERIAL PRIMARY KEY,
    storage_batch_code VARCHAR(64) NOT NULL UNIQUE,
    source_type VARCHAR(32) NOT NULL,
    source_bind_batch_code VARCHAR(64),
    warehouse_code VARCHAR(64) NOT NULL,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    storage_status VARCHAR(32) NOT NULL,
    remark VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_arc_storage_batch_source CHECK (source_type IN ('BIND_GUIDED', 'DIRECT')),
    CONSTRAINT ck_arc_storage_batch_status CHECK (storage_status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_arc_storage_batch_status
    ON arc_storage_batch (storage_status, created_at DESC);

CREATE TABLE IF NOT EXISTS arc_storage_batch_item (
    storage_item_id BIGSERIAL PRIMARY KEY,
    storage_batch_id BIGINT NOT NULL REFERENCES arc_storage_batch (storage_batch_id),
    item_type VARCHAR(32) NOT NULL,
    volume_id BIGINT,
    archive_id BIGINT,
    bind_volume_code VARCHAR(64),
    archive_code VARCHAR(64),
    location_code VARCHAR(64) NOT NULL,
    result_status VARCHAR(32) NOT NULL,
    error_message VARCHAR(1000),
    stored_at TIMESTAMP,
    CONSTRAINT ck_arc_storage_batch_item_type CHECK (item_type IN ('VOLUME', 'ARCHIVE')),
    CONSTRAINT ck_arc_storage_batch_item_status CHECK (result_status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_arc_storage_batch_item_batch
    ON arc_storage_batch_item (storage_batch_id, result_status);

CREATE TABLE IF NOT EXISTS arc_storage_ledger (
    ledger_id BIGSERIAL PRIMARY KEY,
    ledger_code VARCHAR(64) NOT NULL UNIQUE,
    storage_batch_code VARCHAR(64) NOT NULL,
    item_type VARCHAR(32) NOT NULL,
    bind_batch_code VARCHAR(64),
    bind_volume_code VARCHAR(64),
    archive_code VARCHAR(64),
    warehouse_code VARCHAR(64) NOT NULL,
    location_code VARCHAR(64) NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    result_status VARCHAR(32) NOT NULL,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operation_summary VARCHAR(1000),
    CONSTRAINT ck_arc_storage_ledger_item_type CHECK (item_type IN ('VOLUME', 'ARCHIVE')),
    CONSTRAINT ck_arc_storage_ledger_action CHECK (action_type IN ('STORE')),
    CONSTRAINT ck_arc_storage_ledger_status CHECK (result_status IN ('SUCCESS', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_arc_storage_ledger_batch
    ON arc_storage_ledger (storage_batch_code, operation_time DESC);

CREATE INDEX IF NOT EXISTS idx_arc_storage_ledger_search
    ON arc_storage_ledger (bind_volume_code, archive_code, warehouse_code, location_code, result_status, operation_time DESC);
