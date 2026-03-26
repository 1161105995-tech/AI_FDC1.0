package com.smartarchive.warehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wh_rack")
public class WarehouseRack {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseCode;
    private String areaCode;
    private String rackCode;
    private String rackName;
    private Integer layerCount;
    private Integer slotCount;
    private Integer startX;
    private Integer startY;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}