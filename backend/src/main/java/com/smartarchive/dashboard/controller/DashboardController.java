package com.smartarchive.dashboard.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.dashboard.dto.DashboardSummaryResponse;
import com.smartarchive.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> summary() {
        return ApiResponse.success(dashboardService.getSummary());
    }
}
