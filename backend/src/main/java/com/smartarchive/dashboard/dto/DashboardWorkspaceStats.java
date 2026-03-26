package com.smartarchive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardWorkspaceStats {
    private long pendingCount;
    private long dueTodayCount;
    private long overdueCount;
    private long highPriorityCount;
}
