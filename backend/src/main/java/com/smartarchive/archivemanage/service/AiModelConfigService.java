package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.AiModelConfigCommand;
import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.AiModelConnectionTestResult;
import java.util.List;

public interface AiModelConfigService {
    List<AiModelConfigResponse> list(String keyword);
    AiModelConfigResponse detail(Long modelConfigId);
    AiModelConfigResponse save(Long modelConfigId, AiModelConfigCommand command);
    AiModelConnectionTestResult testConnection(AiModelConfigCommand command);
    AiModelConnectionTestResult testConnection(Long modelConfigId);
    AiModelConfigResponse activate(Long modelConfigId);
    AiModelConfigResponse deactivate(Long modelConfigId);
}