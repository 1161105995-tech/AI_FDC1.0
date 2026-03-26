package com.smartarchive.security.service.impl;

import com.smartarchive.security.dto.SecurityPolicyDto;
import com.smartarchive.security.service.SecurityPolicyService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SecurityPolicyServiceImpl implements SecurityPolicyService {
    @Override
    public List<SecurityPolicyDto> listPolicies() {
        return List.of(
            new SecurityPolicyDto("WATERMARK", "预览与下载水印", "CONTENT_PROTECTION", "ACTIVE"),
            new SecurityPolicyDto("MASKING", "敏感字段脱敏", "DATA_PROTECTION", "ACTIVE"),
            new SecurityPolicyDto("AUDIT", "全量审计留痕", "AUDIT", "ACTIVE"),
            new SecurityPolicyDto("EXPORT_APPROVAL", "导出审批控制", "ACCESS_CONTROL", "ACTIVE")
        );
    }
}
