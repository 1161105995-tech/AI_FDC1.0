package com.smartarchive.warehouse.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseVisualizationResponse {
    private String warehouseCode;
    private String warehouseName;
    private Integer totalLocations;
    private Integer occupiedLocations;
    private Integer warningLocations;
    private List<WarehouseVisualNode> nodes;
}
