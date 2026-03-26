package com.smartarchive.security.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.security.dto.SecurityPolicyDto;
import com.smartarchive.security.service.SecurityPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/policies")
@RequiredArgsConstructor
public class SecurityPolicyController {
    private final SecurityPolicyService securityPolicyService;

    @GetMapping
    public ApiResponse<List<SecurityPolicyDto>> list() {
        return ApiResponse.success(securityPolicyService.listPolicies());
    }
}
