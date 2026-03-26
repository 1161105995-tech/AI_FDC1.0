package com.smartarchive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardMetric {
    private String code;
    private String label;
    private String value;
    private String trend;
}
