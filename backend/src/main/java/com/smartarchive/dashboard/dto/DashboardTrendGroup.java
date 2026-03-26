package com.smartarchive.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardTrendGroup {
    private String range;
    private List<DashboardTrendPoint> points;
}
