package com.smartarchive.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class WarehouseUpdateCommand {
    @NotBlank
    private String warehouseName;
    @NotBlank
    private String warehouseType;
    @NotBlank
    private String managerName;
    private String contactPhone;
    private String address;
    private BigDecimal areaSize;
    private String photoUrl;
    private String status;
    private String description;
}