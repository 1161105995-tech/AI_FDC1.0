package com.smartarchive.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardRecentItem {
    private String id;
    private String title;
    private String type;
    private String time;
    private String route;
}
