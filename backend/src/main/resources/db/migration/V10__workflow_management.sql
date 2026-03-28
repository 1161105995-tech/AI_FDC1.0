-- 创建工作流实例表
CREATE TABLE IF NOT EXISTS wf_workflow_instance (
    id SERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255) NOT NULL,
    business_key VARCHAR(255) NOT NULL,
    process_definition_id VARCHAR(255),
    process_definition_key VARCHAR(255),
    process_definition_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    business_type VARCHAR(100),
    business_id BIGINT,
    initiator_id VARCHAR(255) NOT NULL,
    initiator_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    variables TEXT,
    delete_flag VARCHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建工作流任务表
CREATE TABLE IF NOT EXISTS wf_workflow_task (
    id SERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    process_instance_id VARCHAR(255) NOT NULL,
    task_definition_key VARCHAR(255),
    task_name VARCHAR(255),
    assignee VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    business_key VARCHAR(255),
    create_time TIMESTAMP NOT NULL,
    complete_time TIMESTAMP,
    delete_flag VARCHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建工作流历史表
CREATE TABLE IF NOT EXISTS wf_workflow_history (
    id SERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255) NOT NULL,
    task_id VARCHAR(255),
    activity_id VARCHAR(255),
    activity_name VARCHAR(255),
    assignee VARCHAR(255),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration BIGINT,
    delete_flag VARCHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_wf_workflow_instance_process_id ON wf_workflow_instance(process_instance_id);
CREATE INDEX IF NOT EXISTS idx_wf_workflow_instance_business_key ON wf_workflow_instance(business_key);
CREATE INDEX IF NOT EXISTS idx_wf_workflow_task_process_id ON wf_workflow_task(process_instance_id);
CREATE INDEX IF NOT EXISTS idx_wf_workflow_task_task_id ON wf_workflow_task(task_id);
CREATE INDEX IF NOT EXISTS idx_wf_workflow_history_process_id ON wf_workflow_history(process_instance_id);
