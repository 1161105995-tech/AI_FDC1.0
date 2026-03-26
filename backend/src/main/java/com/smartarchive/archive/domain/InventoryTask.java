package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("arc_inventory_task")
public class InventoryTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private String warehouseCode;
    private String inventoryScope;
    private String taskStatus;
    private Integer abnormalCount;
    private String owner;
    private LocalDate dueDate;
}