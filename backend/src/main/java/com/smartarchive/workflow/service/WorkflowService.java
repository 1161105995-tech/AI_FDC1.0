package com.smartarchive.workflow.service;

import com.smartarchive.workflow.dto.WorkflowDefinitionDto;
import java.util.List;

public interface WorkflowService {
    List<WorkflowDefinitionDto> listDefinitions();
}
