package com.smartarchive.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardTaskBucket {
    private String key;
    private String label;
    private List<DashboardTaskItem> tasks;
}
