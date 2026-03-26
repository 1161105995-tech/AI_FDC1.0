package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.domain.WorkflowInstance;

public interface WorkflowInstanceService extends IService<WorkflowInstance> {
    WorkflowInstance start(String definitionCode, String businessKey, String businessType, String currentNode);
}
