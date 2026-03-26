package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseAreaCreateCommand {
    @NotBlank
    private String warehouseCode;
    @NotBlank
    private String areaCode;
    @NotBlank
    private String areaName;
    private Integer startX;
    private Integer startY;
    private Integer width;
    private Integer height;
    private String status;
}