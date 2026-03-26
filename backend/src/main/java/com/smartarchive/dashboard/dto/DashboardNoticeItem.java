package com.smartarchive.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardNoticeItem {
    private String id;
    private String category;
    private String title;
    private String summary;
    private String time;
    private String tagType;
    private String route;
}
