CREATE TABLE IF NOT EXISTS arc_archive_object (
    id BIGSERIAL PRIMARY KEY,
    archive_code VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    archive_type VARCHAR(64) NOT NULL,
    classification_code VARCHAR(64),
    security_level VARCHAR(32) NOT NULL,
    retention_period VARCHAR(64) NOT NULL,
    organization_name VARCHAR(128) NOT NULL,
    fonds_name VARCHAR(128),
    carrier_type VARCHAR(32) NOT NULL,
    physical_status VARCHAR(32) NOT NULL,
    digital_status VARCHAR(32) NOT NULL,
    current_workflow_stage VARCHAR(64) NOT NULL,
    current_warehouse_code VARCHAR(64),
    current_location_code VARCHAR(64),
    responsible_person VARCHAR(64),
    formed_date DATE,
    file_count INTEGER DEFAULT 0,
    page_count INTEGER DEFAULT 0,
    ai_classified BOOLEAN DEFAULT FALSE,
    ai_metadata_extracted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS wh_location (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    area_code VARCHAR(64) NOT NULL,
    shelf_code VARCHAR(64) NOT NULL,
    layer_code VARCHAR(64) NOT NULL,
    location_code VARCHAR(64) NOT NULL UNIQUE,
    location_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    capacity INTEGER NOT NULL DEFAULT 0,
    occupied_count INTEGER NOT NULL DEFAULT 0,
    x INTEGER NOT NULL DEFAULT 0,
    y INTEGER NOT NULL DEFAULT 0,
    width INTEGER NOT NULL DEFAULT 120,
    height INTEGER NOT NULL DEFAULT 72,
    utilization_rate NUMERIC(5, 2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

INSERT INTO arc_archive_object (
    archive_code, title, archive_type, classification_code, security_level,
    retention_period, organization_name, fonds_name, carrier_type, physical_status,
    digital_status, current_workflow_stage, current_warehouse_code, current_location_code,
    responsible_person, formed_date, file_count, page_count, ai_classified, ai_metadata_extracted
) VALUES
('ARC-2026-0001', '2025年度重大合同归档', '合同档案', 'HT-01', 'SECRET', '30年', '法务部', '综合档案全宗', 'PAPER_ELECTRONIC', 'IN_STORAGE', 'AVAILABLE', 'ARCHIVED', 'WH-001', 'WH-001-A-01-01', '张敏', '2025-12-31', 8, 126, TRUE, TRUE),
('ARC-2026-0002', '核心岗位人事档案', '人事档案', 'HR-03', 'CONFIDENTIAL', '长期', '人力资源部', '人事档案全宗', 'PAPER_ELECTRONIC', 'BORROWED', 'RESTRICTED', 'BORROWED', 'WH-001', 'WH-001-B-02-03', '李楠', '2026-01-08', 5, 88, TRUE, TRUE),
('ARC-2026-0003', '设备采购审批资料', '项目档案', 'XM-07', 'INTERNAL', '10年', '采购部', '项目档案全宗', 'ELECTRONIC', 'VIRTUAL', 'AVAILABLE', 'REVIEWING', 'WH-001', 'WH-001-A-02-02', '王倩', '2026-02-18', 12, 54, FALSE, TRUE)
ON CONFLICT (archive_code) DO NOTHING;

INSERT INTO wh_location (
    warehouse_code, warehouse_name, area_code, shelf_code, layer_code, location_code,
    location_name, status, capacity, occupied_count, x, y, width, height, utilization_rate
) VALUES
('WH-001', '一号综合档案库', 'A', '01', '01', 'WH-001-A-01-01', 'A区01架01层01位', 'OCCUPIED', 40, 36, 40, 40, 140, 76, 0.90),
('WH-001', '一号综合档案库', 'A', '02', '02', 'WH-001-A-02-02', 'A区02架02层02位', 'FREE', 40, 8, 220, 40, 140, 76, 0.20),
('WH-001', '一号综合档案库', 'B', '02', '03', 'WH-001-B-02-03', 'B区02架03层03位', 'WARNING', 40, 38, 400, 40, 140, 76, 0.95),
('WH-001', '一号综合档案库', 'B', '03', '01', 'WH-001-B-03-01', 'B区03架01层01位', 'ABNORMAL', 40, 30, 580, 40, 140, 76, 0.75)
ON CONFLICT (location_code) DO NOTHING;

CREATE TABLE IF NOT EXISTS arc_archive_receipt (
    id BIGSERIAL PRIMARY KEY,
    receipt_code VARCHAR(64) NOT NULL UNIQUE,
    source_dept VARCHAR(128) NOT NULL,
    archive_title VARCHAR(255) NOT NULL,
    archive_type VARCHAR(64) NOT NULL,
    security_level VARCHAR(32) NOT NULL,
    receive_status VARCHAR(32) NOT NULL,
    workflow_instance_code VARCHAR(64),
    submitted_by VARCHAR(64) NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS arc_catalog_task (
    id BIGSERIAL PRIMARY KEY,
    task_code VARCHAR(64) NOT NULL UNIQUE,
    archive_code VARCHAR(64) NOT NULL,
    archive_title VARCHAR(255) NOT NULL,
    task_status VARCHAR(32) NOT NULL,
    assignee VARCHAR(64) NOT NULL,
    due_date DATE
);

CREATE TABLE IF NOT EXISTS wf_workflow_instance (
    id BIGSERIAL PRIMARY KEY,
    instance_code VARCHAR(64) NOT NULL UNIQUE,
    definition_code VARCHAR(64) NOT NULL,
    business_key VARCHAR(64) NOT NULL,
    business_type VARCHAR(64) NOT NULL,
    current_node VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO wf_workflow_instance (instance_code, definition_code, business_key, business_type, current_node, status)
VALUES ('WFI-20260323001', 'ARC_RECEIVE', 'REC-20260323001', 'ARCHIVE_RECEIPT', 'RECEIVE_REVIEW', 'RUNNING')
ON CONFLICT (instance_code) DO NOTHING;

INSERT INTO arc_archive_receipt (receipt_code, source_dept, archive_title, archive_type, security_level, receive_status, workflow_instance_code, submitted_by)
VALUES ('REC-20260323001', '综合办公室', '2026年第一季度综合文书归档', '综合文书档案', 'INTERNAL', 'PENDING_REVIEW', 'WFI-20260323001', '系统管理员')
ON CONFLICT (receipt_code) DO NOTHING;

INSERT INTO arc_catalog_task (task_code, archive_code, archive_title, task_status, assignee, due_date)
VALUES ('CAT-20260323001', 'ARC-2026-0001', '2025年度重大合同归档', 'PENDING', '档案管理员', CURRENT_DATE + INTERVAL '3 day')
ON CONFLICT (task_code) DO NOTHING;

CREATE TABLE IF NOT EXISTS arc_borrow_record (
    id BIGSERIAL PRIMARY KEY,
    borrow_code VARCHAR(64) NOT NULL UNIQUE,
    archive_code VARCHAR(64) NOT NULL,
    archive_title VARCHAR(255) NOT NULL,
    borrower VARCHAR(64) NOT NULL,
    borrow_type VARCHAR(32) NOT NULL,
    approval_status VARCHAR(32) NOT NULL,
    borrow_status VARCHAR(32) NOT NULL,
    expected_return_date DATE,
    borrowed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS arc_inventory_task (
    id BIGSERIAL PRIMARY KEY,
    task_code VARCHAR(64) NOT NULL UNIQUE,
    warehouse_code VARCHAR(64) NOT NULL,
    inventory_scope VARCHAR(255) NOT NULL,
    task_status VARCHAR(32) NOT NULL,
    abnormal_count INTEGER DEFAULT 0,
    owner VARCHAR(64) NOT NULL,
    due_date DATE
);

CREATE TABLE IF NOT EXISTS arc_disposal_record (
    id BIGSERIAL PRIMARY KEY,
    disposal_code VARCHAR(64) NOT NULL UNIQUE,
    archive_code VARCHAR(64) NOT NULL,
    archive_title VARCHAR(255) NOT NULL,
    retention_period VARCHAR(64) NOT NULL,
    appraisal_conclusion VARCHAR(255) NOT NULL,
    approval_status VARCHAR(32) NOT NULL,
    disposal_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO arc_borrow_record (borrow_code, archive_code, archive_title, borrower, borrow_type, approval_status, borrow_status, expected_return_date)
VALUES ('BOR-20260324001', 'ARC-2026-0002', '核心岗位人事档案', '王晓峰', 'PHYSICAL', 'APPROVED', 'BORROWED', CURRENT_DATE + INTERVAL '7 day')
ON CONFLICT (borrow_code) DO NOTHING;

INSERT INTO arc_inventory_task (task_code, warehouse_code, inventory_scope, task_status, abnormal_count, owner, due_date)
VALUES ('INV-20260324001', 'WH-001', '一号综合档案库季度盘点', 'IN_PROGRESS', 1, '档案管理员', CURRENT_DATE + INTERVAL '5 day')
ON CONFLICT (task_code) DO NOTHING;

INSERT INTO arc_disposal_record (disposal_code, archive_code, archive_title, retention_period, appraisal_conclusion, approval_status, disposal_status)
VALUES ('DSP-20260324001', 'ARC-2016-0018', '2016年度临时采购资料', '10年', '达到销毁条件，建议销毁', 'PENDING', 'UNDER_APPRAISAL')
ON CONFLICT (disposal_code) DO NOTHING;
CREATE TABLE IF NOT EXISTS wh_warehouse (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL UNIQUE,
    warehouse_name VARCHAR(128) NOT NULL,
    warehouse_type VARCHAR(64) NOT NULL,
    manager_name VARCHAR(64) NOT NULL,
    contact_phone VARCHAR(32),
    address VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO wh_warehouse (warehouse_code, warehouse_name, warehouse_type, manager_name, contact_phone, address, status, description)
VALUES ('WH-001', '一号综合档案库', '综合档案库', '档案管理员', '13800000000', '档案中心一层', 'ACTIVE', '用于存放综合类纸质与电子档案对应实体')
ON CONFLICT (warehouse_code) DO NOTHING;
CREATE TABLE IF NOT EXISTS wh_area (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL,
    area_code VARCHAR(64) NOT NULL,
    area_name VARCHAR(128) NOT NULL,
    sort_order INTEGER DEFAULT 1,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (warehouse_code, area_code)
);

CREATE TABLE IF NOT EXISTS wh_rack (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL,
    area_code VARCHAR(64) NOT NULL,
    rack_code VARCHAR(64) NOT NULL,
    rack_name VARCHAR(128) NOT NULL,
    layer_count INTEGER NOT NULL,
    slot_count INTEGER NOT NULL,
    start_x INTEGER DEFAULT 40,
    start_y INTEGER DEFAULT 40,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (warehouse_code, rack_code)
);

INSERT INTO wh_area (warehouse_code, area_code, area_name, sort_order, status)
VALUES ('WH-001', 'A', '区域A区', 1, 'ACTIVE')
ON CONFLICT (warehouse_code, area_code) DO NOTHING;

INSERT INTO wh_rack (warehouse_code, area_code, rack_code, rack_name, layer_count, slot_count, start_x, start_y, status)
VALUES
('WH-001', 'A', 'GZ-07', '柜子B', 10, 10, 40, 40, 'NORMAL'),
('WH-001', 'A', 'GZ-08', '柜子B(副本)', 10, 10, 260, 40, 'NORMAL')
ON CONFLICT (warehouse_code, rack_code) DO NOTHING;