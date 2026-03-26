package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRackCreateCommand {
    @NotBlank
    private String warehouseCode;
    @NotBlank
    private String areaCode;
    @NotBlank
    private String rackCode;
    @NotBlank
    private String rackName;
    @Min(1)
    private Integer layerCount;
    @Min(1)
    private Integer slotCount;
    private Integer startX;
    private Integer startY;
}