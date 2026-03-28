package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_storage_batch")
public class StorageBatch {
    @TableId(type = IdType.AUTO)
    private Long storageBatchId;
    private String storageBatchCode;
    private String sourceType;
    private String sourceBindBatchCode;
    private String warehouseCode;
    private Long operatorId;
    private String operatorName;
    private String storageStatus;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
