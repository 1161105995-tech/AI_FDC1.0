package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseLocationUpdateCommand {
    @NotBlank
    private String locationName;
    @NotBlank
    private String status;
    @Min(1)
    private Integer capacity;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
}
