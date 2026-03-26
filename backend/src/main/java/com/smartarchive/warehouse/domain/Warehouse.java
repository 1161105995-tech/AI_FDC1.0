package com.smartarchive.warehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wh_warehouse")
public class Warehouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String warehouseType;
    private String managerName;
    private String contactPhone;
    private String address;
    private BigDecimal areaSize;
    private String photoUrl;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}