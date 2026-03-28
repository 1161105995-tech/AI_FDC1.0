package com.smartarchive.workflow.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.workflow.domain.WorkflowInstance;
import com.smartarchive.workflow.domain.WorkflowTask;
import com.smartarchive.workflow.dto.CompleteTaskCommand;
import com.smartarchive.workflow.dto.DelegateTaskCommand;
import com.smartarchive.workflow.dto.MergeProcessesCommand;
import com.smartarchive.workflow.dto.RejectTaskCommand;
import com.smartarchive.workflow.dto.SplitProcessCommand;
import com.smartarchive.workflow.dto.StartProcessCommand;
import com.smartarchive.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/processes")
    public ApiResponse<WorkflowInstance> startProcess(@Valid @RequestBody StartProcessCommand command) {
        return ApiResponse.success(workflowService.startProcess(command));
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ApiResponse<Void> completeTask(@PathVariable String taskId, @Valid @RequestBody CompleteTaskCommand command) {
        command.setTaskId(taskId);
        workflowService.completeTask(command);
        return ApiResponse.success(null);
    }

    @PostMapping("/tasks/{taskId}/reject")
    public ApiResponse<Void> rejectTask(@PathVariable String taskId, @Valid @RequestBody RejectTaskCommand command) {
        command.setTaskId(taskId);
        workflowService.rejectTask(command);
        return ApiResponse.success(null);
    }

    @PostMapping("/tasks/{taskId}/delegate")
    public ApiResponse<Void> delegateTask(@PathVariable String taskId, @Valid @RequestBody DelegateTaskCommand command) {
        command.setTaskId(taskId);
        workflowService.delegateTask(command);
        return ApiResponse.success(null);
    }

    @GetMapping("/tasks/my")
    public ApiResponse<List<WorkflowTask>> getMyTasks(@RequestParam String userId) {
        return ApiResponse.success(workflowService.getMyTasks(userId));
    }

    @GetMapping("/processes")
    public ApiResponse<List<WorkflowInstance>> listProcesses(@RequestParam(required = false) String processDefinitionKey) {
        return ApiResponse.success(workflowService.listProcesses(processDefinitionKey));
    }

    @GetMapping("/processes/my")
    public ApiResponse<List<WorkflowInstance>> getMyProcesses(@RequestParam String userId) {
        return ApiResponse.success(workflowService.getMyProcesses(userId));
    }

    @GetMapping("/processes/participated")
    public ApiResponse<List<WorkflowInstance>> getMyParticipatedProcesses(@RequestParam String userId) {
        return ApiResponse.success(workflowService.getMyParticipatedProcesses(userId));
    }

    @PostMapping("/processes/split")
    public ApiResponse<List<WorkflowInstance>> splitProcess(@Valid @RequestBody SplitProcessCommand command) {
        return ApiResponse.success(workflowService.splitProcess(command));
    }

    @PostMapping("/processes/merge")
    public ApiResponse<WorkflowInstance> mergeProcesses(@Valid @RequestBody MergeProcessesCommand command) {
        return ApiResponse.success(workflowService.mergeProcesses(command));
    }

    @GetMapping("/processes/{processInstanceId}")
    public ApiResponse<WorkflowInstance> getProcessInstance(@PathVariable String processInstanceId) {
        return ApiResponse.success(workflowService.getProcessInstance(processInstanceId));
    }

    @GetMapping("/processes/{processInstanceId}/tasks")
    public ApiResponse<List<WorkflowTask>> getProcessTasks(@PathVariable String processInstanceId) {
        return ApiResponse.success(workflowService.getProcessTasks(processInstanceId));
    }

    @GetMapping("/tasks/{taskId}")
    public ApiResponse<WorkflowTask> getTask(@PathVariable String taskId) {
        return ApiResponse.success(workflowService.getTask(taskId));
    }
}
