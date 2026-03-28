package com.smartarchive.workflow.dto;

import java.util.List;
import java.util.Map;

public class MergeProcessesCommand {
    private List<String> childProcessInstanceIds;
    private String parentProcessDefinitionKey;
    private String parentBusinessKey;
    private String businessType;
    private Long businessId;
    private String initiatorId;
    private String initiatorName;
    private Map<String, Object> variables;

    // getters and setters
    public List<String> getChildProcessInstanceIds() {
        return childProcessInstanceIds;
    }

    public void setChildProcessInstanceIds(List<String> childProcessInstanceIds) {
        this.childProcessInstanceIds = childProcessInstanceIds;
    }

    public String getParentProcessDefinitionKey() {
        return parentProcessDefinitionKey;
    }

    public void setParentProcessDefinitionKey(String parentProcessDefinitionKey) {
        this.parentProcessDefinitionKey = parentProcessDefinitionKey;
    }

    public String getParentBusinessKey() {
        return parentBusinessKey;
    }

    public void setParentBusinessKey(String parentBusinessKey) {
        this.parentBusinessKey = parentBusinessKey;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}