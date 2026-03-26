package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.AiModelConfigCommand;
import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.AiModelConnectionTestResult;
import com.smartarchive.archivemanage.service.AiModelConfigService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/governance/ai-model-configs")
@RequiredArgsConstructor
public class AiModelConfigController {
    private final AiModelConfigService aiModelConfigService;

    @GetMapping
    public ApiResponse<List<AiModelConfigResponse>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(aiModelConfigService.list(keyword));
    }

    @GetMapping("/{modelConfigId}")
    public ApiResponse<AiModelConfigResponse> detail(@PathVariable Long modelConfigId) {
        return ApiResponse.success(aiModelConfigService.detail(modelConfigId));
    }

    @PostMapping
    public ApiResponse<AiModelConfigResponse> create(@Valid @RequestBody AiModelConfigCommand command) {
        return ApiResponse.success(aiModelConfigService.save(null, command));
    }

    @PutMapping("/{modelConfigId}")
    public ApiResponse<AiModelConfigResponse> update(@PathVariable Long modelConfigId,
                                                     @Valid @RequestBody AiModelConfigCommand command) {
        return ApiResponse.success(aiModelConfigService.save(modelConfigId, command));
    }

    @PostMapping("/test-connection")
    public ApiResponse<AiModelConnectionTestResult> testConnection(@Valid @RequestBody AiModelConfigCommand command) {
        return ApiResponse.success(aiModelConfigService.testConnection(command));
    }

    @PostMapping("/{modelConfigId}/test-connection")
    public ApiResponse<AiModelConnectionTestResult> testSavedConnection(@PathVariable Long modelConfigId) {
        return ApiResponse.success(aiModelConfigService.testConnection(modelConfigId));
    }

    @PostMapping("/{modelConfigId}/activate")
    public ApiResponse<AiModelConfigResponse> activate(@PathVariable Long modelConfigId) {
        return ApiResponse.success(aiModelConfigService.activate(modelConfigId));
    }

    @PostMapping("/{modelConfigId}/deactivate")
    public ApiResponse<AiModelConfigResponse> deactivate(@PathVariable Long modelConfigId) {
        return ApiResponse.success(aiModelConfigService.deactivate(modelConfigId));
    }
}