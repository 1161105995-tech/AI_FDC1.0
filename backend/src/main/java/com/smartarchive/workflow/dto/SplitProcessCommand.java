package com.smartarchive.workflow.dto;

import java.util.List;
import java.util.Map;

public class SplitProcessCommand {
    private String parentProcessInstanceId;
    private String childProcessDefinitionKey;
    private List<String> childBusinessKeys;
    private String businessType;
    private Long businessId;
    private String initiatorId;
    private String initiatorName;
    private Map<String, Object> variables;

    // getters and setters
    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public String getChildProcessDefinitionKey() {
        return childProcessDefinitionKey;
    }

    public void setChildProcessDefinitionKey(String childProcessDefinitionKey) {
        this.childProcessDefinitionKey = childProcessDefinitionKey;
    }

    public List<String> getChildBusinessKeys() {
        return childBusinessKeys;
    }

    public void setChildBusinessKeys(List<String> childBusinessKeys) {
        this.childBusinessKeys = childBusinessKeys;
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