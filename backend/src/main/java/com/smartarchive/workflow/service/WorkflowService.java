package com.smartarchive.workflow.service;

import com.smartarchive.workflow.domain.WorkflowInstance;
import com.smartarchive.workflow.domain.WorkflowTask;
import com.smartarchive.workflow.dto.*;
import java.util.List;

public interface WorkflowService {
    // 启动工作流
    WorkflowInstance startProcess(StartProcessCommand command);
    
    // 完成任务
    void completeTask(CompleteTaskCommand command);
    
    // 驳回任务
    void rejectTask(RejectTaskCommand command);
    
    // 转审任务
    void delegateTask(DelegateTaskCommand command);
    
    // 获取我的待办任务
    List<WorkflowTask> getMyTasks(String userId);
    
    // 获取我的申请
    List<WorkflowInstance> getMyProcesses(String userId);
    
    // 获取我参与的工作流
    List<WorkflowInstance> getMyParticipatedProcesses(String userId);
    
    // 拆分主单为子单
    List<WorkflowInstance> splitProcess(SplitProcessCommand command);
    
    // 合并子单为父单
    WorkflowInstance mergeProcesses(MergeProcessesCommand command);
    
    // 获取工作流实例详情
    WorkflowInstance getProcessInstance(String processInstanceId);
    
    // 获取任务详情
    WorkflowTask getTask(String taskId);

    // 获取流程任务列表
    List<WorkflowTask> getProcessTasks(String processInstanceId);

    // 获取流程实例列表
    List<WorkflowInstance> listProcesses(String processDefinitionKey);
}
