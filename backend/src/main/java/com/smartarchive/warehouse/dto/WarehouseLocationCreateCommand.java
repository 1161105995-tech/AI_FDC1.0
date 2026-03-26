package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseLocationCreateCommand {
    @NotBlank
    private String warehouseCode;
    @NotBlank
    private String areaCode;
    @NotBlank
    private String shelfCode;
    @NotBlank
    private String layerCode;
    @NotBlank
    private String locationCode;
    @NotBlank
    private String locationName;
    @Min(1)
    private Integer capacity;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
}