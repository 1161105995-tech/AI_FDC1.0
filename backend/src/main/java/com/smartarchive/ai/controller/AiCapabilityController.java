package com.smartarchive.ai.controller;

import com.smartarchive.ai.dto.AiCapabilityDto;
import com.smartarchive.ai.service.AiCapabilityService;
import com.smartarchive.common.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/capabilities")
@RequiredArgsConstructor
public class AiCapabilityController {
    private final AiCapabilityService aiCapabilityService;

    @GetMapping
    public ApiResponse<List<AiCapabilityDto>> list() {
        return ApiResponse.success(aiCapabilityService.listCapabilities());
    }
}
