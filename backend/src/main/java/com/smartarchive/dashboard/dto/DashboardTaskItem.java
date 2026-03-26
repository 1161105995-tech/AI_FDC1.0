package com.smartarchive.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardTaskItem {
    private String title;
    private String businessType;
    private String currentStep;
    private String initiator;
    private String deadline;
    private String priority;
    private String status;
    private String route;
}
