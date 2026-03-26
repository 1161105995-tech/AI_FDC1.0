package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseAreaCopyCommand {
    @NotBlank
    private String targetAreaCode;
    @NotBlank
    private String targetAreaName;
}
