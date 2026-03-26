package com.smartarchive.warehouse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseVisualNode {
    private Long id;
    private String warehouseCode;
    private String locationCode;
    private String locationName;
    private String status;
    private Integer capacity;
    private Integer occupiedCount;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private String color;
}
