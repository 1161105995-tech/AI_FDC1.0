package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.command.AssignLocationCommand;
import com.smartarchive.archive.domain.ArchiveObject;
import com.smartarchive.archive.mapper.ArchiveObjectMapper;
import com.smartarchive.archive.dto.ArchiveObjectQueryRequest;
import com.smartarchive.archive.service.ArchiveObjectService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.warehouse.domain.WarehouseLocation;
import com.smartarchive.warehouse.service.WarehouseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ArchiveObjectServiceImpl extends ServiceImpl<ArchiveObjectMapper, ArchiveObject> implements ArchiveObjectService {
    private final WarehouseService warehouseService;

    @Override
    public List<ArchiveObject> search(ArchiveObjectQueryRequest request) {
        LambdaQueryWrapper<ArchiveObject> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(query -> query.like(ArchiveObject::getTitle, request.getKeyword()).or().like(ArchiveObject::getArchiveCode, request.getKeyword()));
        }
        wrapper.eq(StringUtils.hasText(request.getArchiveType()), ArchiveObject::getArchiveType, request.getArchiveType())
            .eq(StringUtils.hasText(request.getSecurityLevel()), ArchiveObject::getSecurityLevel, request.getSecurityLevel())
            .eq(StringUtils.hasText(request.getOrganizationName()), ArchiveObject::getOrganizationName, request.getOrganizationName())
            .orderByDesc(ArchiveObject::getUpdatedAt);
        return list(wrapper);
    }

    @Override
    public boolean assignLocation(AssignLocationCommand command) {
        ArchiveObject archiveObject = getById(command.getArchiveObjectId());
        if (archiveObject == null) {
            throw new BusinessException("档案对象不存在");
        }
        WarehouseLocation location = warehouseService.lambdaQuery().eq(WarehouseLocation::getLocationCode, command.getLocationCode()).one();
        if (location == null) {
            throw new BusinessException("库位不存在");
        }
        archiveObject.setCurrentWarehouseCode(location.getWarehouseCode());
        archiveObject.setCurrentLocationCode(location.getLocationCode());
        archiveObject.setPhysicalStatus("IN_STORAGE");
        boolean updated = updateById(archiveObject);
        location.setOccupiedCount(Math.min(location.getCapacity(), location.getOccupiedCount() + 1));
        location.setStatus(location.getOccupiedCount() >= location.getCapacity() * 0.9 ? "WARNING" : "OCCUPIED");
        warehouseService.updateById(location);
        return updated;
    }
}
