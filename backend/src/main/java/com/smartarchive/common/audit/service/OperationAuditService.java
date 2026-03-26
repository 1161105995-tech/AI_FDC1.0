package com.smartarchive.common.audit.service;

import com.smartarchive.common.audit.dto.AuditRecordResponse;
import java.util.List;

public interface OperationAuditService {
    void record(String moduleCode,
                String moduleName,
                String businessType,
                String businessKey,
                String operationType,
                String operationSummary,
                Object beforeSnapshot,
                Object afterSnapshot,
                Long operatorId,
                String operatorName);

    List<AuditRecordResponse> listByModule(String moduleCode);
}
