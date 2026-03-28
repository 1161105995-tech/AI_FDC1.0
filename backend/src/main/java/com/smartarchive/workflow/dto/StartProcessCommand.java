package com.smartarchive.workflow.dto;

import java.util.Map;
import java.util.HashMap;

public class StartProcessCommand {
    private String processDefinitionKey;
    private String businessKey;
    private Object variables;
    private String businessType;
    private Long businessId;
    private String initiatorId;
    private String initiatorName;

    // getters and setters
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getVariables() {
        if (variables instanceof Map) {
            return (Map<String, Object>) variables;
        }
        return new HashMap<>();
    }

    public void setVariables(Object variables) {
        this.variables = variables;
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
}