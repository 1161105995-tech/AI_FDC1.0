package com.smartarchive.common.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartarchive.common.audit.domain.OperationAuditRecord;
import com.smartarchive.common.audit.dto.AuditRecordResponse;
import com.smartarchive.common.audit.mapper.OperationAuditRecordMapper;
import com.smartarchive.common.audit.service.OperationAuditService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationAuditServiceImpl implements OperationAuditService {
    private final OperationAuditRecordMapper operationAuditRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void record(String moduleCode,
                       String moduleName,
                       String businessType,
                       String businessKey,
                       String operationType,
                       String operationSummary,
                       Object beforeSnapshot,
                       Object afterSnapshot,
                       Long operatorId,
                       String operatorName) {
        OperationAuditRecord record = new OperationAuditRecord();
        record.setModuleCode(moduleCode);
        record.setModuleName(moduleName);
        record.setBusinessType(businessType);
        record.setBusinessKey(businessKey);
        record.setOperationType(operationType);
        record.setOperationSummary(operationSummary);
        record.setBeforeSnapshot(toJson(beforeSnapshot));
        record.setAfterSnapshot(toJson(afterSnapshot));
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setOperationTime(LocalDateTime.now());
        operationAuditRecordMapper.insert(record);
    }

    @Override
    public List<AuditRecordResponse> listByModule(String moduleCode) {
        return operationAuditRecordMapper.selectList(new LambdaQueryWrapper<OperationAuditRecord>()
                .eq(OperationAuditRecord::getModuleCode, moduleCode)
                .orderByDesc(OperationAuditRecord::getOperationTime)
                .last("limit 20"))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AuditRecordResponse toResponse(OperationAuditRecord record) {
        AuditRecordResponse response = new AuditRecordResponse();
        response.setId(record.getId());
        response.setModuleCode(record.getModuleCode());
        response.setModuleName(record.getModuleName());
        response.setBusinessType(record.getBusinessType());
        response.setBusinessKey(record.getBusinessKey());
        response.setOperationType(record.getOperationType());
        response.setOperationSummary(record.getOperationSummary());
        response.setBeforeSnapshot(record.getBeforeSnapshot());
        response.setAfterSnapshot(record.getAfterSnapshot());
        response.setOperatorId(record.getOperatorId());
        response.setOperatorName(record.getOperatorName());
        response.setOperationTime(record.getOperationTime());
        return response;
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }
}
