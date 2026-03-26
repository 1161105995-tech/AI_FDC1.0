package com.smartarchive.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.warehouse.domain.Warehouse;
import com.smartarchive.warehouse.domain.WarehouseArea;
import com.smartarchive.warehouse.domain.WarehouseLocation;
import com.smartarchive.warehouse.domain.WarehouseRack;
import com.smartarchive.warehouse.dto.WarehouseAreaCopyCommand;
import com.smartarchive.warehouse.dto.WarehouseAreaCreateCommand;
import com.smartarchive.warehouse.dto.WarehouseAreaUpdateCommand;
import com.smartarchive.warehouse.dto.WarehouseCopyCommand;
import com.smartarchive.warehouse.dto.WarehouseCreateCommand;
import com.smartarchive.warehouse.dto.WarehouseLocationCreateCommand;
import com.smartarchive.warehouse.dto.WarehouseLocationUpdateCommand;
import com.smartarchive.warehouse.dto.WarehouseManagementResponse;
import com.smartarchive.warehouse.dto.WarehouseRackCopyCommand;
import com.smartarchive.warehouse.dto.WarehouseRackCreateCommand;
import com.smartarchive.warehouse.dto.WarehouseRackUpdateCommand;
import com.smartarchive.warehouse.dto.WarehouseSummaryResponse;
import com.smartarchive.warehouse.dto.WarehouseUpdateCommand;
import com.smartarchive.warehouse.dto.WarehouseVisualizationResponse;
import java.util.List;

public interface WarehouseService extends IService<WarehouseLocation> {
    List<WarehouseSummaryResponse> listWarehouses();
    Warehouse createWarehouse(WarehouseCreateCommand command);
    Warehouse updateWarehouse(String warehouseCode, WarehouseUpdateCommand command);
    Warehouse copyWarehouse(String warehouseCode, WarehouseCopyCommand command);
    void deleteWarehouse(String warehouseCode);
    WarehouseArea createArea(WarehouseAreaCreateCommand command);
    WarehouseArea updateArea(String warehouseCode, String areaCode, WarehouseAreaUpdateCommand command);
    WarehouseArea copyArea(String warehouseCode, String areaCode, WarehouseAreaCopyCommand command);
    void deleteArea(String warehouseCode, String areaCode);
    WarehouseRack createRack(WarehouseRackCreateCommand command);
    WarehouseRack updateRack(String warehouseCode, String rackCode, WarehouseRackUpdateCommand command);
    WarehouseRack copyRack(String warehouseCode, String rackCode, WarehouseRackCopyCommand command);
    void deleteRack(String warehouseCode, String rackCode);
    WarehouseLocation createLocation(WarehouseLocationCreateCommand command);
    WarehouseLocation updateLocation(String locationCode, WarehouseLocationUpdateCommand command);
    void deleteLocation(String locationCode);
    WarehouseManagementResponse getManagement(String warehouseCode);
    WarehouseVisualizationResponse getVisualization(String warehouseCode);
    List<WarehouseLocation> listLocations(String warehouseCode);
}