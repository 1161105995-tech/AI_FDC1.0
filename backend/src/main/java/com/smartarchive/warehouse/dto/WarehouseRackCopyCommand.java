package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRackCopyCommand {
    @NotBlank
    private String targetRackCode;
    @NotBlank
    private String targetRackName;
    private String targetAreaCode;
}
