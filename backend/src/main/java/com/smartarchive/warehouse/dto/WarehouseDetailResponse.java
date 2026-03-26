package com.smartarchive.warehouse.dto;

import com.smartarchive.warehouse.domain.Warehouse;
import com.smartarchive.warehouse.domain.WarehouseLocation;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseDetailResponse {
    private Warehouse warehouse;
    private List<WarehouseLocation> locations;
}