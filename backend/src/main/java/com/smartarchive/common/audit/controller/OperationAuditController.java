package com.smartarchive.common.audit.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.common.audit.dto.AuditRecordResponse;
import com.smartarchive.common.audit.service.OperationAuditService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common/audits")
@RequiredArgsConstructor
public class OperationAuditController {
    private final OperationAuditService operationAuditService;

    @GetMapping("/modules/{moduleCode}")
    public ApiResponse<List<AuditRecordResponse>> listByModule(@PathVariable String moduleCode) {
        return ApiResponse.success(operationAuditService.listByModule(moduleCode));
    }
}
