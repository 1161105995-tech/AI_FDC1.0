package com.smartarchive.workflow.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.workflow.dto.WorkflowDefinitionDto;
import com.smartarchive.workflow.service.WorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;

    @GetMapping("/definitions")
    public ApiResponse<List<WorkflowDefinitionDto>> definitions() {
        return ApiResponse.success(workflowService.listDefinitions());
    }
}
