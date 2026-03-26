package com.smartarchive.common.audit.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuditRecordResponse {
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String businessType;
    private String businessKey;
    private String operationType;
    private String operationSummary;
    private String beforeSnapshot;
    private String afterSnapshot;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operationTime;
}
