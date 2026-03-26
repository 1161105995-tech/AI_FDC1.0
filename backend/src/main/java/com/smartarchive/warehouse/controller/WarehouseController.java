package com.smartarchive.warehouse.controller;

import com.smartarchive.common.api.ApiResponse;
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
import com.smartarchive.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public ApiResponse<List<WarehouseSummaryResponse>> listWarehouses() {
        return ApiResponse.success(warehouseService.listWarehouses());
    }

    @PostMapping
    public ApiResponse<Warehouse> createWarehouse(@Valid @RequestBody WarehouseCreateCommand command) {
        return ApiResponse.success(warehouseService.createWarehouse(command));
    }

    @PutMapping("/{warehouseCode}")
    public ApiResponse<Warehouse> updateWarehouse(@PathVariable String warehouseCode, @Valid @RequestBody WarehouseUpdateCommand command) {
        return ApiResponse.success(warehouseService.updateWarehouse(warehouseCode, command));
    }

    @PostMapping("/{warehouseCode}/copy")
    public ApiResponse<Warehouse> copyWarehouse(@PathVariable String warehouseCode, @Valid @RequestBody WarehouseCopyCommand command) {
        return ApiResponse.success(warehouseService.copyWarehouse(warehouseCode, command));
    }

    @DeleteMapping("/{warehouseCode}")
    public ApiResponse<Boolean> deleteWarehouse(@PathVariable String warehouseCode) {
        warehouseService.deleteWarehouse(warehouseCode);
        return ApiResponse.success(Boolean.TRUE);
    }

    @PostMapping("/areas")
    public ApiResponse<WarehouseArea> createArea(@Valid @RequestBody WarehouseAreaCreateCommand command) {
        return ApiResponse.success(warehouseService.createArea(command));
    }

    @PutMapping("/{warehouseCode}/areas/{areaCode}")
    public ApiResponse<WarehouseArea> updateArea(
        @PathVariable String warehouseCode,
        @PathVariable String areaCode,
        @Valid @RequestBody WarehouseAreaUpdateCommand command
    ) {
        return ApiResponse.success(warehouseService.updateArea(warehouseCode, areaCode, command));
    }

    @PostMapping("/{warehouseCode}/areas/{areaCode}/copy")
    public ApiResponse<WarehouseArea> copyArea(
        @PathVariable String warehouseCode,
        @PathVariable String areaCode,
        @Valid @RequestBody WarehouseAreaCopyCommand command
    ) {
        return ApiResponse.success(warehouseService.copyArea(warehouseCode, areaCode, command));
    }

    @DeleteMapping("/{warehouseCode}/areas/{areaCode}")
    public ApiResponse<Boolean> deleteArea(@PathVariable String warehouseCode, @PathVariable String areaCode) {
        warehouseService.deleteArea(warehouseCode, areaCode);
        return ApiResponse.success(Boolean.TRUE);
    }

    @PostMapping("/racks")
    public ApiResponse<WarehouseRack> createRack(@Valid @RequestBody WarehouseRackCreateCommand command) {
        return ApiResponse.success(warehouseService.createRack(command));
    }

    @PutMapping("/{warehouseCode}/racks/{rackCode}")
    public ApiResponse<WarehouseRack> updateRack(
        @PathVariable String warehouseCode,
        @PathVariable String rackCode,
        @Valid @RequestBody WarehouseRackUpdateCommand command
    ) {
        return ApiResponse.success(warehouseService.updateRack(warehouseCode, rackCode, command));
    }

    @PostMapping("/{warehouseCode}/racks/{rackCode}/copy")
    public ApiResponse<WarehouseRack> copyRack(
        @PathVariable String warehouseCode,
        @PathVariable String rackCode,
        @Valid @RequestBody WarehouseRackCopyCommand command
    ) {
        return ApiResponse.success(warehouseService.copyRack(warehouseCode, rackCode, command));
    }

    @DeleteMapping("/{warehouseCode}/racks/{rackCode}")
    public ApiResponse<Boolean> deleteRack(@PathVariable String warehouseCode, @PathVariable String rackCode) {
        warehouseService.deleteRack(warehouseCode, rackCode);
        return ApiResponse.success(Boolean.TRUE);
    }

    @PostMapping("/locations")
    public ApiResponse<WarehouseLocation> createLocation(@Valid @RequestBody WarehouseLocationCreateCommand command) {
        return ApiResponse.success(warehouseService.createLocation(command));
    }

    @PutMapping("/locations/{locationCode}")
    public ApiResponse<WarehouseLocation> updateLocation(@PathVariable String locationCode, @Valid @RequestBody WarehouseLocationUpdateCommand command) {
        return ApiResponse.success(warehouseService.updateLocation(locationCode, command));
    }

    @DeleteMapping("/locations/{locationCode}")
    public ApiResponse<Boolean> deleteLocation(@PathVariable String locationCode) {
        warehouseService.deleteLocation(locationCode);
        return ApiResponse.success(Boolean.TRUE);
    }

    @GetMapping("/{warehouseCode}/management")
    public ApiResponse<WarehouseManagementResponse> management(@PathVariable String warehouseCode) {
        return ApiResponse.success(warehouseService.getManagement(warehouseCode));
    }

    @GetMapping("/{warehouseCode}/visualization")
    public ApiResponse<WarehouseVisualizationResponse> visualization(@PathVariable String warehouseCode) {
        return ApiResponse.success(warehouseService.getVisualization(warehouseCode));
    }
}