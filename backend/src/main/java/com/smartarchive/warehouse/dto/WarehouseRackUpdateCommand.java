package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRackUpdateCommand {
    @NotBlank
    private String rackName;
    private String status;
    private Integer startX;
    private Integer startY;
}
