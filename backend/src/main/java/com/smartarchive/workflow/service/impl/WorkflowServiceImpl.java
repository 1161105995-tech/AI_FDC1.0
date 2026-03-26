package com.smartarchive.workflow.service.impl;

import com.smartarchive.workflow.dto.WorkflowDefinitionDto;
import com.smartarchive.workflow.service.WorkflowService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowServiceImpl implements WorkflowService {
    @Override
    public List<WorkflowDefinitionDto> listDefinitions() {
        return List.of(
            new WorkflowDefinitionDto("ARC_RECEIVE", "归档接收流程", "档案接收与审核", "ACTIVE"),
            new WorkflowDefinitionDto("ARC_BORROW", "借阅审批流程", "档案借阅", "ACTIVE"),
            new WorkflowDefinitionDto("ARC_DESTROY", "销毁鉴定流程", "档案销毁", "ACTIVE")
        );
    }
}
