package com.smartarchive.warehouse.dto;

import com.smartarchive.warehouse.domain.Warehouse;
import com.smartarchive.warehouse.domain.WarehouseArea;
import com.smartarchive.warehouse.domain.WarehouseLocation;
import com.smartarchive.warehouse.domain.WarehouseRack;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseManagementResponse {
    private Warehouse warehouse;
    private List<WarehouseArea> areas;
    private List<WarehouseRack> racks;
    private List<WarehouseLocation> locations;
}