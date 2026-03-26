package com.smartarchive.warehouse.dto;

import com.smartarchive.warehouse.domain.Warehouse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseSummaryResponse {
    private Warehouse warehouse;
    private Integer areaCount;
    private Integer rackCount;
    private Integer locationCount;
    private Integer freeLocationCount;
    private Integer occupiedLocationCount;
    private LocalDateTime lastUpdatedAt;
}