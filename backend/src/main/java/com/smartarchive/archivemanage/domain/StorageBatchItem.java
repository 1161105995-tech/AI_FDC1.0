package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_storage_batch_item")
public class StorageBatchItem {
    @TableId(type = IdType.AUTO)
    private Long storageItemId;
    private Long storageBatchId;
    private String itemType;
    private Long volumeId;
    private Long archiveId;
    private String bindVolumeCode;
    private String archiveCode;
    private String locationCode;
    private String resultStatus;
    private String errorMessage;
    private LocalDateTime storedAt;
}
