package com.smartarchive.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkflowDefinitionDto {
    private String code;
    private String name;
    private String scenario;
    private String status;
}
