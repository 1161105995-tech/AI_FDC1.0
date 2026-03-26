package com.smartarchive.rule.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.rule.dto.RuleDefinitionDto;
import com.smartarchive.rule.service.RuleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {
    private final RuleService ruleService;

    @GetMapping
    public ApiResponse<List<RuleDefinitionDto>> list() {
        return ApiResponse.success(ruleService.listRules());
    }
}
