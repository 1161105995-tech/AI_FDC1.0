package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.domain.WorkflowInstance;
import com.smartarchive.archive.mapper.ArchiveWorkflowInstanceMapper;
import com.smartarchive.archive.service.WorkflowInstanceService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class WorkflowInstanceServiceImpl extends ServiceImpl<ArchiveWorkflowInstanceMapper, WorkflowInstance> implements WorkflowInstanceService {
    @Override
    public WorkflowInstance start(String definitionCode, String businessKey, String businessType, String currentNode) {
        WorkflowInstance instance = new WorkflowInstance();
        instance.setInstanceCode("WFI-" + System.currentTimeMillis());
        instance.setDefinitionCode(definitionCode);
        instance.setBusinessKey(businessKey);
        instance.setBusinessType(businessType);
        instance.setCurrentNode(currentNode);
        instance.setStatus("RUNNING");
        instance.setStartedAt(LocalDateTime.now());
        save(instance);
        return instance;
    }
}
