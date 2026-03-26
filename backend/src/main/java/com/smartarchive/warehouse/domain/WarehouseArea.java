package com.smartarchive.warehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wh_area")
public class WarehouseArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseCode;
    private String areaCode;
    private String areaName;
    private Integer sortOrder;
    private Integer startX;
    private Integer startY;
    private Integer width;
    private Integer height;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}