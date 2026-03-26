package com.smartarchive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardDistributionItem {
    private String label;
    private int value;
    private String color;
}
