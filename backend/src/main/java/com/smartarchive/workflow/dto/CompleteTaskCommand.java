package com.smartarchive.workflow.dto;

import java.util.Map;

public class CompleteTaskCommand {
    private String taskId;
    private Map<String, Object> variables;

    // getters and setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}