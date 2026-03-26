package com.smartarchive.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
    private List<DashboardMetric> metrics;
    private DashboardWorkspaceStats workspaceStats;
    private List<DashboardTaskBucket> workspaceBuckets;
    private List<DashboardNoticeItem> notifications;
    private List<DashboardRecentItem> recentActivities;
    private List<DashboardTrendGroup> archiveTrends;
    private List<DashboardTrendGroup> borrowTrends;
    private List<DashboardDistributionItem> distributions;
    private List<DashboardRiskIndicator> riskIndicators;
}
