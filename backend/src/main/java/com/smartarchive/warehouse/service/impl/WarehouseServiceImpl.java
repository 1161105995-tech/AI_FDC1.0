package com.smartarchive.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.common.exception.BusinessException;
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
import com.smartarchive.warehouse.dto.WarehouseVisualNode;
import com.smartarchive.warehouse.dto.WarehouseVisualizationResponse;
import com.smartarchive.warehouse.mapper.WarehouseAreaMapper;
import com.smartarchive.warehouse.mapper.WarehouseLocationMapper;
import com.smartarchive.warehouse.mapper.WarehouseMapper;
import com.smartarchive.warehouse.mapper.WarehouseRackMapper;
import com.smartarchive.warehouse.service.WarehouseService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseLocationMapper, WarehouseLocation> implements WarehouseService {
    private final WarehouseMapper warehouseMapper;
    private final WarehouseAreaMapper warehouseAreaMapper;
    private final WarehouseRackMapper warehouseRackMapper;

    private static final Map<String, String> STATUS_COLORS = Map.of(
        "FREE", "#d9f7be",
        "OCCUPIED", "#95de64",
        "WARNING", "#ffd666",
        "SEALED", "#bfbfbf",
        "ABNORMAL", "#ff7875"
    );

    @Override
    public List<WarehouseSummaryResponse> listWarehouses() {
        List<Warehouse> warehouses = warehouseMapper.selectList(new LambdaQueryWrapper<Warehouse>()
            .orderByDesc(Warehouse::getUpdatedAt)
            .orderByAsc(Warehouse::getWarehouseCode));
        List<WarehouseArea> areas = warehouseAreaMapper.selectList(new LambdaQueryWrapper<WarehouseArea>());
        List<WarehouseRack> racks = warehouseRackMapper.selectList(new LambdaQueryWrapper<WarehouseRack>());
        List<WarehouseLocation> locations = list();

        Map<String, Long> areaCountMap = areas.stream().collect(Collectors.groupingBy(WarehouseArea::getWarehouseCode, Collectors.counting()));
        Map<String, Long> rackCountMap = racks.stream().collect(Collectors.groupingBy(WarehouseRack::getWarehouseCode, Collectors.counting()));
        Map<String, Long> locationCountMap = locations.stream().collect(Collectors.groupingBy(WarehouseLocation::getWarehouseCode, Collectors.counting()));
        Map<String, Long> freeLocationCountMap = locations.stream().filter(item -> "FREE".equals(item.getStatus()))
            .collect(Collectors.groupingBy(WarehouseLocation::getWarehouseCode, Collectors.counting()));
        Map<String, Long> occupiedLocationCountMap = locations.stream().filter(item -> !"FREE".equals(item.getStatus()))
            .collect(Collectors.groupingBy(WarehouseLocation::getWarehouseCode, Collectors.counting()));

        return warehouses.stream().map(warehouse -> WarehouseSummaryResponse.builder()
            .warehouse(warehouse)
            .areaCount(areaCountMap.getOrDefault(warehouse.getWarehouseCode(), 0L).intValue())
            .rackCount(rackCountMap.getOrDefault(warehouse.getWarehouseCode(), 0L).intValue())
            .locationCount(locationCountMap.getOrDefault(warehouse.getWarehouseCode(), 0L).intValue())
            .freeLocationCount(freeLocationCountMap.getOrDefault(warehouse.getWarehouseCode(), 0L).intValue())
            .occupiedLocationCount(occupiedLocationCountMap.getOrDefault(warehouse.getWarehouseCode(), 0L).intValue())
            .lastUpdatedAt(warehouse.getUpdatedAt())
            .build()).toList();
    }

    @Override
    @Transactional
    public Warehouse createWarehouse(WarehouseCreateCommand command) {
        assertWarehouseCodeNotExists(command.getWarehouseCode());
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(command.getWarehouseCode());
        warehouse.setWarehouseName(command.getWarehouseName());
        warehouse.setWarehouseType(command.getWarehouseType());
        warehouse.setManagerName(command.getManagerName());
        warehouse.setContactPhone(command.getContactPhone());
        warehouse.setAddress(command.getAddress());
        warehouse.setAreaSize(command.getAreaSize());
        warehouse.setPhotoUrl(command.getPhotoUrl());
        warehouse.setDescription(command.getDescription());
        warehouse.setStatus(StringUtils.hasText(command.getStatus()) ? command.getStatus() : "ACTIVE");
        warehouse.setCreatedAt(LocalDateTime.now());
        warehouse.setUpdatedAt(LocalDateTime.now());
        warehouseMapper.insert(warehouse);
        return warehouse;
    }

    @Override
    @Transactional
    public Warehouse updateWarehouse(String warehouseCode, WarehouseUpdateCommand command) {
        Warehouse warehouse = ensureWarehouseExists(warehouseCode);
        warehouse.setWarehouseName(command.getWarehouseName());
        warehouse.setWarehouseType(command.getWarehouseType());
        warehouse.setManagerName(command.getManagerName());
        warehouse.setContactPhone(command.getContactPhone());
        warehouse.setAddress(command.getAddress());
        warehouse.setAreaSize(command.getAreaSize());
        warehouse.setPhotoUrl(command.getPhotoUrl());
        warehouse.setDescription(command.getDescription());
        if (StringUtils.hasText(command.getStatus())) {
            warehouse.setStatus(command.getStatus());
        }
        warehouse.setUpdatedAt(LocalDateTime.now());
        warehouseMapper.updateById(warehouse);
        return warehouse;
    }

    @Override
    @Transactional
    public Warehouse copyWarehouse(String warehouseCode, WarehouseCopyCommand command) {
        Warehouse source = ensureWarehouseExists(warehouseCode);
        assertWarehouseCodeNotExists(command.getTargetWarehouseCode());

        Warehouse target = new Warehouse();
        target.setWarehouseCode(command.getTargetWarehouseCode());
        target.setWarehouseName(command.getTargetWarehouseName());
        target.setWarehouseType(source.getWarehouseType());
        target.setManagerName(StringUtils.hasText(command.getManagerName()) ? command.getManagerName() : source.getManagerName());
        target.setContactPhone(StringUtils.hasText(command.getContactPhone()) ? command.getContactPhone() : source.getContactPhone());
        target.setAddress(StringUtils.hasText(command.getAddress()) ? command.getAddress() : source.getAddress());
        target.setAreaSize(command.getAreaSize() != null ? command.getAreaSize() : source.getAreaSize());
        target.setPhotoUrl(StringUtils.hasText(command.getPhotoUrl()) ? command.getPhotoUrl() : source.getPhotoUrl());
        target.setDescription(StringUtils.hasText(command.getDescription()) ? command.getDescription() : source.getDescription());
        target.setStatus(StringUtils.hasText(command.getStatus()) ? command.getStatus() : source.getStatus());
        target.setCreatedAt(LocalDateTime.now());
        target.setUpdatedAt(LocalDateTime.now());
        warehouseMapper.insert(target);

        List<WarehouseArea> sourceAreas = listAreas(warehouseCode);
        List<WarehouseRack> sourceRacks = listRacks(warehouseCode);
        List<WarehouseLocation> sourceLocations = listLocations(warehouseCode);

        for (WarehouseArea sourceArea : sourceAreas) {
            WarehouseArea areaCopy = cloneArea(sourceArea, target.getWarehouseCode(), sourceArea.getAreaCode(), sourceArea.getAreaName());
            warehouseAreaMapper.insert(areaCopy);
        }

        for (WarehouseRack sourceRack : sourceRacks) {
            WarehouseRack rackCopy = cloneRack(sourceRack, target.getWarehouseCode(), sourceRack.getAreaCode(), sourceRack.getRackCode(), sourceRack.getRackName());
            warehouseRackMapper.insert(rackCopy);
        }

        for (WarehouseLocation sourceLocation : sourceLocations) {
            WarehouseLocation locationCopy = cloneLocation(sourceLocation, target.getWarehouseCode(), target.getWarehouseName(), sourceLocation.getAreaCode(), sourceLocation.getShelfCode(), sourceLocation.getLocationCode().replace(source.getWarehouseCode(), target.getWarehouseCode()), sourceLocation.getLocationName());
            save(locationCopy);
        }
        return target;
    }

    @Override
    @Transactional
    public void deleteWarehouse(String warehouseCode) {
        ensureWarehouseExists(warehouseCode);
        ensureLocationsDeletable(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode));
        remove(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode));
        warehouseRackMapper.delete(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode));
        warehouseAreaMapper.delete(new LambdaQueryWrapper<WarehouseArea>().eq(WarehouseArea::getWarehouseCode, warehouseCode));
        warehouseMapper.delete(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getWarehouseCode, warehouseCode));
    }

    @Override
    @Transactional
    public WarehouseArea createArea(WarehouseAreaCreateCommand command) {
        ensureWarehouseExists(command.getWarehouseCode());
        assertAreaCodeNotExists(command.getWarehouseCode(), command.getAreaCode());
        WarehouseArea area = new WarehouseArea();
        area.setWarehouseCode(command.getWarehouseCode());
        area.setAreaCode(command.getAreaCode());
        area.setAreaName(command.getAreaName());
        area.setSortOrder(nextAreaSort(command.getWarehouseCode()));
        area.setStartX(command.getStartX() == null ? 0 : command.getStartX());
        area.setStartY(command.getStartY() == null ? 0 : command.getStartY());
        area.setWidth(command.getWidth() == null ? 620 : command.getWidth());
        area.setHeight(command.getHeight() == null ? 260 : command.getHeight());
        area.setStatus(StringUtils.hasText(command.getStatus()) ? command.getStatus() : "ACTIVE");
        area.setCreatedAt(LocalDateTime.now());
        area.setUpdatedAt(LocalDateTime.now());
        warehouseAreaMapper.insert(area);
        touchWarehouse(command.getWarehouseCode());
        return area;
    }

    @Override
    @Transactional
    public WarehouseArea updateArea(String warehouseCode, String areaCode, WarehouseAreaUpdateCommand command) {
        WarehouseArea area = ensureAreaExists(warehouseCode, areaCode);
        area.setAreaName(command.getAreaName());
        if (command.getStartX() != null) { area.setStartX(command.getStartX()); }
        if (command.getStartY() != null) { area.setStartY(command.getStartY()); }
        if (command.getWidth() != null) { area.setWidth(command.getWidth()); }
        if (command.getHeight() != null) { area.setHeight(command.getHeight()); }
        if (StringUtils.hasText(command.getStatus())) { area.setStatus(command.getStatus()); }
        area.setUpdatedAt(LocalDateTime.now());
        warehouseAreaMapper.updateById(area);
        touchWarehouse(warehouseCode);
        return area;
    }

    @Override
    @Transactional
    public WarehouseArea copyArea(String warehouseCode, String areaCode, WarehouseAreaCopyCommand command) {
        Warehouse warehouse = ensureWarehouseExists(warehouseCode);
        WarehouseArea sourceArea = ensureAreaExists(warehouseCode, areaCode);
        assertAreaCodeNotExists(warehouseCode, command.getTargetAreaCode());

        WarehouseArea areaCopy = cloneArea(sourceArea, warehouseCode, command.getTargetAreaCode(), command.getTargetAreaName());
        areaCopy.setSortOrder(nextAreaSort(warehouseCode));
        warehouseAreaMapper.insert(areaCopy);

        List<WarehouseRack> sourceRacks = listRacks(warehouseCode).stream().filter(item -> areaCode.equals(item.getAreaCode())).toList();
        List<WarehouseLocation> sourceLocations = listLocations(warehouseCode).stream().filter(item -> areaCode.equals(item.getAreaCode())).toList();

        for (WarehouseRack sourceRack : sourceRacks) {
            String rackCodeCopy = command.getTargetAreaCode() + "-" + sourceRack.getRackCode();
            assertRackCodeNotExists(warehouseCode, rackCodeCopy);
            String rackNameCopy = sourceRack.getRackName() + "-副本";
            WarehouseRack rackCopy = cloneRack(sourceRack, warehouseCode, command.getTargetAreaCode(), rackCodeCopy, rackNameCopy);
            warehouseRackMapper.insert(rackCopy);

            List<WarehouseLocation> rackLocations = sourceLocations.stream().filter(item -> sourceRack.getRackCode().equals(item.getShelfCode())).toList();
            for (WarehouseLocation sourceLocation : rackLocations) {
                String targetLocationCode = sourceLocation.getLocationCode().replace("-" + areaCode + "-", "-" + command.getTargetAreaCode() + "-").replace("-" + sourceRack.getRackCode() + "-", "-" + rackCodeCopy + "-");
                WarehouseLocation locationCopy = cloneLocation(sourceLocation, warehouseCode, warehouse.getWarehouseName(), command.getTargetAreaCode(), rackCodeCopy, targetLocationCode, sourceLocation.getLocationName().replace(sourceRack.getRackName(), rackNameCopy));
                save(locationCopy);
            }
        }
        touchWarehouse(warehouseCode);
        return areaCopy;
    }

    @Override
    @Transactional
    public void deleteArea(String warehouseCode, String areaCode) {
        ensureAreaExists(warehouseCode, areaCode);
        ensureLocationsDeletable(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode).eq(WarehouseLocation::getAreaCode, areaCode));
        remove(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode).eq(WarehouseLocation::getAreaCode, areaCode));
        warehouseRackMapper.delete(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode).eq(WarehouseRack::getAreaCode, areaCode));
        warehouseAreaMapper.delete(new LambdaQueryWrapper<WarehouseArea>().eq(WarehouseArea::getWarehouseCode, warehouseCode).eq(WarehouseArea::getAreaCode, areaCode));
        touchWarehouse(warehouseCode);
    }

    @Override
    @Transactional
    public WarehouseRack createRack(WarehouseRackCreateCommand command) {
        Warehouse warehouse = ensureWarehouseExists(command.getWarehouseCode());
        ensureAreaExists(command.getWarehouseCode(), command.getAreaCode());
        assertRackCodeNotExists(command.getWarehouseCode(), command.getRackCode());
        WarehouseRack rack = new WarehouseRack();
        rack.setWarehouseCode(command.getWarehouseCode());
        rack.setAreaCode(command.getAreaCode());
        rack.setRackCode(command.getRackCode());
        rack.setRackName(command.getRackName());
        rack.setLayerCount(command.getLayerCount());
        rack.setSlotCount(command.getSlotCount());
        rack.setStartX(command.getStartX() == null ? 40 : command.getStartX());
        rack.setStartY(command.getStartY() == null ? 40 : command.getStartY());
        rack.setStatus("NORMAL");
        rack.setCreatedAt(LocalDateTime.now());
        rack.setUpdatedAt(LocalDateTime.now());
        warehouseRackMapper.insert(rack);
        generateLocations(warehouse, rack);
        touchWarehouse(command.getWarehouseCode());
        touchArea(command.getWarehouseCode(), command.getAreaCode());
        return rack;
    }

    @Override
    @Transactional
    public WarehouseRack updateRack(String warehouseCode, String rackCode, WarehouseRackUpdateCommand command) {
        WarehouseRack rack = ensureRackExists(warehouseCode, rackCode);
        rack.setRackName(command.getRackName());
        if (StringUtils.hasText(command.getStatus())) { rack.setStatus(command.getStatus()); }
        if (command.getStartX() != null) { rack.setStartX(command.getStartX()); }
        if (command.getStartY() != null) { rack.setStartY(command.getStartY()); }
        rack.setUpdatedAt(LocalDateTime.now());
        warehouseRackMapper.updateById(rack);
        touchWarehouse(warehouseCode);
        touchArea(warehouseCode, rack.getAreaCode());
        return rack;
    }

    @Override
    @Transactional
    public WarehouseRack copyRack(String warehouseCode, String rackCode, WarehouseRackCopyCommand command) {
        Warehouse warehouse = ensureWarehouseExists(warehouseCode);
        WarehouseRack sourceRack = ensureRackExists(warehouseCode, rackCode);
        String targetAreaCode = StringUtils.hasText(command.getTargetAreaCode()) ? command.getTargetAreaCode() : sourceRack.getAreaCode();
        ensureAreaExists(warehouseCode, targetAreaCode);
        assertRackCodeNotExists(warehouseCode, command.getTargetRackCode());

        WarehouseRack rackCopy = cloneRack(sourceRack, warehouseCode, targetAreaCode, command.getTargetRackCode(), command.getTargetRackName());
        warehouseRackMapper.insert(rackCopy);

        List<WarehouseLocation> sourceLocations = listLocations(warehouseCode).stream().filter(item -> rackCode.equals(item.getShelfCode())).toList();
        for (WarehouseLocation sourceLocation : sourceLocations) {
            String targetLocationCode = sourceLocation.getLocationCode().replace("-" + sourceRack.getAreaCode() + "-", "-" + targetAreaCode + "-").replace("-" + rackCode + "-", "-" + command.getTargetRackCode() + "-");
            WarehouseLocation locationCopy = cloneLocation(sourceLocation, warehouseCode, warehouse.getWarehouseName(), targetAreaCode, command.getTargetRackCode(), targetLocationCode, sourceLocation.getLocationName().replace(sourceRack.getRackName(), command.getTargetRackName()));
            save(locationCopy);
        }
        touchWarehouse(warehouseCode);
        touchArea(warehouseCode, targetAreaCode);
        return rackCopy;
    }

    @Override
    @Transactional
    public void deleteRack(String warehouseCode, String rackCode) {
        WarehouseRack rack = ensureRackExists(warehouseCode, rackCode);
        ensureLocationsDeletable(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode).eq(WarehouseLocation::getShelfCode, rackCode));
        remove(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getWarehouseCode, warehouseCode).eq(WarehouseLocation::getShelfCode, rackCode));
        warehouseRackMapper.delete(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode).eq(WarehouseRack::getRackCode, rackCode));
        touchWarehouse(warehouseCode);
        touchArea(warehouseCode, rack.getAreaCode());
    }

    @Override
    @Transactional
    public WarehouseLocation createLocation(WarehouseLocationCreateCommand command) {
        Warehouse warehouse = ensureWarehouseExists(command.getWarehouseCode());
        ensureAreaExists(command.getWarehouseCode(), command.getAreaCode());
        ensureRackExists(command.getWarehouseCode(), command.getShelfCode());
        WarehouseLocation exists = getOne(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getLocationCode, command.getLocationCode()));
        if (exists != null) { throw new BusinessException("Location code already exists"); }
        WarehouseLocation location = new WarehouseLocation();
        location.setWarehouseCode(warehouse.getWarehouseCode());
        location.setWarehouseName(warehouse.getWarehouseName());
        location.setAreaCode(command.getAreaCode());
        location.setShelfCode(command.getShelfCode());
        location.setLayerCode(command.getLayerCode());
        location.setLocationCode(command.getLocationCode());
        location.setLocationName(command.getLocationName());
        location.setStatus("FREE");
        location.setCapacity(command.getCapacity());
        location.setOccupiedCount(0);
        location.setX(command.getX() == null ? 40 : command.getX());
        location.setY(command.getY() == null ? 40 : command.getY());
        location.setWidth(command.getWidth() == null ? 140 : command.getWidth());
        location.setHeight(command.getHeight() == null ? 76 : command.getHeight());
        location.setUtilizationRate(BigDecimal.ZERO);
        location.setUpdatedAt(LocalDateTime.now());
        location.setDeleted(0);
        save(location);
        touchWarehouse(command.getWarehouseCode());
        touchArea(command.getWarehouseCode(), command.getAreaCode());
        touchRack(command.getWarehouseCode(), command.getShelfCode());
        return location;
    }

    @Override
    @Transactional
    public WarehouseLocation updateLocation(String locationCode, WarehouseLocationUpdateCommand command) {
        WarehouseLocation location = ensureLocationExists(locationCode);
        location.setLocationName(command.getLocationName());
        location.setStatus(command.getStatus());
        location.setCapacity(command.getCapacity());
        if (command.getX() != null) { location.setX(command.getX()); }
        if (command.getY() != null) { location.setY(command.getY()); }
        if (command.getWidth() != null) { location.setWidth(command.getWidth()); }
        if (command.getHeight() != null) { location.setHeight(command.getHeight()); }
        int occupiedCount = "FREE".equals(command.getStatus()) ? 0 : Math.max(1, location.getOccupiedCount() == null ? 0 : location.getOccupiedCount());
        location.setOccupiedCount(occupiedCount);
        location.setUtilizationRate(calculateRate(occupiedCount, command.getCapacity()));
        location.setUpdatedAt(LocalDateTime.now());
        updateById(location);
        touchWarehouse(location.getWarehouseCode());
        touchArea(location.getWarehouseCode(), location.getAreaCode());
        touchRack(location.getWarehouseCode(), location.getShelfCode());
        return location;
    }

    @Override
    @Transactional
    public void deleteLocation(String locationCode) {
        WarehouseLocation location = ensureLocationExists(locationCode);
        if (!"FREE".equals(location.getStatus()) || location.getOccupiedCount() > 0) {
            throw new BusinessException("Current location is occupied and cannot be deleted");
        }
        remove(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getLocationCode, locationCode));
        touchWarehouse(location.getWarehouseCode());
        touchArea(location.getWarehouseCode(), location.getAreaCode());
        touchRack(location.getWarehouseCode(), location.getShelfCode());
    }

    @Override
    public WarehouseManagementResponse getManagement(String warehouseCode) {
        Warehouse warehouse = ensureWarehouseExists(warehouseCode);
        return WarehouseManagementResponse.builder().warehouse(warehouse).areas(listAreas(warehouseCode)).racks(listRacks(warehouseCode)).locations(listLocations(warehouseCode)).build();
    }

    @Override
    public WarehouseVisualizationResponse getVisualization(String warehouseCode) {
        List<WarehouseLocation> locations = listLocations(warehouseCode);
        Warehouse warehouse = ensureWarehouseExists(warehouseCode);
        long occupied = locations.stream().filter(item -> "OCCUPIED".equals(item.getStatus())).count();
        long warning = locations.stream().filter(item -> "WARNING".equals(item.getStatus()) || "ABNORMAL".equals(item.getStatus())).count();
        return WarehouseVisualizationResponse.builder()
            .warehouseCode(warehouse.getWarehouseCode())
            .warehouseName(warehouse.getWarehouseName())
            .totalLocations(locations.size())
            .occupiedLocations((int) occupied)
            .warningLocations((int) warning)
            .nodes(locations.stream().map(this::toNode).collect(Collectors.toList()))
            .build();
    }

    @Override
    public List<WarehouseLocation> listLocations(String warehouseCode) {
        return list(new LambdaQueryWrapper<WarehouseLocation>()
            .eq(StringUtils.hasText(warehouseCode), WarehouseLocation::getWarehouseCode, warehouseCode)
            .orderByAsc(WarehouseLocation::getAreaCode)
            .orderByAsc(WarehouseLocation::getShelfCode)
            .orderByAsc(WarehouseLocation::getLayerCode)
            .orderByAsc(WarehouseLocation::getLocationCode));
    }

    private List<WarehouseArea> listAreas(String warehouseCode) {
        return warehouseAreaMapper.selectList(new LambdaQueryWrapper<WarehouseArea>()
            .eq(WarehouseArea::getWarehouseCode, warehouseCode)
            .orderByAsc(WarehouseArea::getSortOrder)
            .orderByAsc(WarehouseArea::getAreaCode));
    }

    private List<WarehouseRack> listRacks(String warehouseCode) {
        return warehouseRackMapper.selectList(new LambdaQueryWrapper<WarehouseRack>()
            .eq(WarehouseRack::getWarehouseCode, warehouseCode)
            .orderByAsc(WarehouseRack::getAreaCode)
            .orderByAsc(WarehouseRack::getRackCode));
    }

    private Warehouse ensureWarehouseExists(String warehouseCode) {
        Warehouse warehouse = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getWarehouseCode, warehouseCode));
        if (warehouse == null) { throw new BusinessException("Warehouse does not exist"); }
        return warehouse;
    }

    private WarehouseArea ensureAreaExists(String warehouseCode, String areaCode) {
        WarehouseArea area = warehouseAreaMapper.selectOne(new LambdaQueryWrapper<WarehouseArea>().eq(WarehouseArea::getWarehouseCode, warehouseCode).eq(WarehouseArea::getAreaCode, areaCode));
        if (area == null) { throw new BusinessException("Area does not exist"); }
        return area;
    }

    private WarehouseRack ensureRackExists(String warehouseCode, String rackCode) {
        WarehouseRack rack = warehouseRackMapper.selectOne(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode).eq(WarehouseRack::getRackCode, rackCode));
        if (rack == null) { throw new BusinessException("Rack does not exist"); }
        return rack;
    }

    private WarehouseLocation ensureLocationExists(String locationCode) {
        WarehouseLocation location = getOne(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getLocationCode, locationCode));
        if (location == null) { throw new BusinessException("Location does not exist"); }
        return location;
    }

    private void assertWarehouseCodeNotExists(String warehouseCode) {
        Warehouse exists = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getWarehouseCode, warehouseCode));
        if (exists != null) { throw new BusinessException("Warehouse code already exists"); }
    }

    private void assertAreaCodeNotExists(String warehouseCode, String areaCode) {
        WarehouseArea exists = warehouseAreaMapper.selectOne(new LambdaQueryWrapper<WarehouseArea>().eq(WarehouseArea::getWarehouseCode, warehouseCode).eq(WarehouseArea::getAreaCode, areaCode));
        if (exists != null) { throw new BusinessException("Area code already exists"); }
    }

    private void assertRackCodeNotExists(String warehouseCode, String rackCode) {
        WarehouseRack exists = warehouseRackMapper.selectOne(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode).eq(WarehouseRack::getRackCode, rackCode));
        if (exists != null) { throw new BusinessException("Rack code already exists"); }
    }

    private void ensureLocationsDeletable(LambdaQueryWrapper<WarehouseLocation> wrapper) {
        List<WarehouseLocation> locations = list(wrapper);
        boolean hasUsedLocation = locations.stream().anyMatch(item -> !"FREE".equals(item.getStatus()) || item.getOccupiedCount() > 0);
        if (hasUsedLocation) { throw new BusinessException("There are occupied locations under the current object"); }
    }

    private int nextAreaSort(String warehouseCode) {
        return listAreas(warehouseCode).size() + 1;
    }

    private void generateLocations(Warehouse warehouse, WarehouseRack rack) {
        for (int layer = 1; layer <= rack.getLayerCount(); layer++) {
            for (int slot = 1; slot <= rack.getSlotCount(); slot++) {
                WarehouseLocation location = new WarehouseLocation();
                location.setWarehouseCode(rack.getWarehouseCode());
                location.setWarehouseName(warehouse.getWarehouseName());
                location.setAreaCode(rack.getAreaCode());
                location.setShelfCode(rack.getRackCode());
                location.setLayerCode(String.format("%02d", layer));
                location.setLocationCode(String.format("%s-%s-%s-L%02d-S%02d", rack.getWarehouseCode(), rack.getAreaCode(), rack.getRackCode(), layer, slot));
                location.setLocationName(String.format("%s 第%d层 第%d格", rack.getRackName(), layer, slot));
                location.setStatus("FREE");
                location.setCapacity(1);
                location.setOccupiedCount(0);
                location.setX(rack.getStartX());
                location.setY(rack.getStartY());
                location.setWidth(140);
                location.setHeight(76);
                location.setUtilizationRate(BigDecimal.ZERO);
                location.setUpdatedAt(LocalDateTime.now());
                location.setDeleted(0);
                save(location);
            }
        }
    }

    private WarehouseArea cloneArea(WarehouseArea sourceArea, String warehouseCode, String areaCode, String areaName) {
        WarehouseArea areaCopy = new WarehouseArea();
        areaCopy.setWarehouseCode(warehouseCode);
        areaCopy.setAreaCode(areaCode);
        areaCopy.setAreaName(areaName);
        areaCopy.setSortOrder(sourceArea.getSortOrder());
        areaCopy.setStartX(sourceArea.getStartX());
        areaCopy.setStartY(sourceArea.getStartY());
        areaCopy.setWidth(sourceArea.getWidth());
        areaCopy.setHeight(sourceArea.getHeight());
        areaCopy.setStatus(sourceArea.getStatus());
        areaCopy.setCreatedAt(LocalDateTime.now());
        areaCopy.setUpdatedAt(LocalDateTime.now());
        return areaCopy;
    }

    private WarehouseRack cloneRack(WarehouseRack sourceRack, String warehouseCode, String areaCode, String rackCode, String rackName) {
        WarehouseRack rackCopy = new WarehouseRack();
        rackCopy.setWarehouseCode(warehouseCode);
        rackCopy.setAreaCode(areaCode);
        rackCopy.setRackCode(rackCode);
        rackCopy.setRackName(rackName);
        rackCopy.setLayerCount(sourceRack.getLayerCount());
        rackCopy.setSlotCount(sourceRack.getSlotCount());
        rackCopy.setStartX(sourceRack.getStartX());
        rackCopy.setStartY(sourceRack.getStartY());
        rackCopy.setStatus(sourceRack.getStatus());
        rackCopy.setCreatedAt(LocalDateTime.now());
        rackCopy.setUpdatedAt(LocalDateTime.now());
        return rackCopy;
    }

    private WarehouseLocation cloneLocation(WarehouseLocation sourceLocation, String warehouseCode, String warehouseName, String areaCode, String shelfCode, String locationCode, String locationName) {
        WarehouseLocation locationCopy = new WarehouseLocation();
        locationCopy.setWarehouseCode(warehouseCode);
        locationCopy.setWarehouseName(warehouseName);
        locationCopy.setAreaCode(areaCode);
        locationCopy.setShelfCode(shelfCode);
        locationCopy.setLayerCode(sourceLocation.getLayerCode());
        locationCopy.setLocationCode(locationCode);
        locationCopy.setLocationName(locationName);
        locationCopy.setStatus("FREE");
        locationCopy.setCapacity(sourceLocation.getCapacity());
        locationCopy.setOccupiedCount(0);
        locationCopy.setX(sourceLocation.getX());
        locationCopy.setY(sourceLocation.getY());
        locationCopy.setWidth(sourceLocation.getWidth());
        locationCopy.setHeight(sourceLocation.getHeight());
        locationCopy.setUtilizationRate(BigDecimal.ZERO);
        locationCopy.setUpdatedAt(LocalDateTime.now());
        locationCopy.setDeleted(0);
        return locationCopy;
    }

    private WarehouseVisualNode toNode(WarehouseLocation location) {
        return WarehouseVisualNode.builder()
            .id(location.getId())
            .warehouseCode(location.getWarehouseCode())
            .locationCode(location.getLocationCode())
            .locationName(location.getLocationName())
            .status(location.getStatus())
            .capacity(location.getCapacity())
            .occupiedCount(location.getOccupiedCount())
            .x(location.getX())
            .y(location.getY())
            .width(location.getWidth())
            .height(location.getHeight())
            .color(STATUS_COLORS.getOrDefault(location.getStatus(), "#91caff"))
            .build();
    }

    private BigDecimal calculateRate(Integer occupiedCount, Integer capacity) {
        if (capacity == null || capacity <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(occupiedCount == null ? 0 : occupiedCount).divide(BigDecimal.valueOf(capacity), 2, RoundingMode.HALF_UP);
    }

    private void touchWarehouse(String warehouseCode) {
        Warehouse warehouse = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getWarehouseCode, warehouseCode));
        if (warehouse != null) {
            warehouse.setUpdatedAt(LocalDateTime.now());
            warehouseMapper.updateById(warehouse);
        }
    }

    private void touchArea(String warehouseCode, String areaCode) {
        WarehouseArea area = warehouseAreaMapper.selectOne(new LambdaQueryWrapper<WarehouseArea>().eq(WarehouseArea::getWarehouseCode, warehouseCode).eq(WarehouseArea::getAreaCode, areaCode));
        if (area != null) {
            area.setUpdatedAt(LocalDateTime.now());
            warehouseAreaMapper.updateById(area);
        }
    }

    private void touchRack(String warehouseCode, String rackCode) {
        WarehouseRack rack = warehouseRackMapper.selectOne(new LambdaQueryWrapper<WarehouseRack>().eq(WarehouseRack::getWarehouseCode, warehouseCode).eq(WarehouseRack::getRackCode, rackCode));
        if (rack != null) {
            rack.setUpdatedAt(LocalDateTime.now());
            warehouseRackMapper.updateById(rack);
        }
    }
}
