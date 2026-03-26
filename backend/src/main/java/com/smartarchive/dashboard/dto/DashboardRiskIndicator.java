package com.smartarchive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardRiskIndicator {
    private String label;
    private long value;
}
