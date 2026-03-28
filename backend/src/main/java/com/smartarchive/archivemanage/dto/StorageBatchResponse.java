package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageBatchResponse {
    private Long storageBatchId;
    private String storageBatchCode;
    private String sourceType;
    private String sourceBindBatchCode;
    private String warehouseCode;
    private String operatorName;
    private String storageStatus;
    private String remark;
    private LocalDateTime createdAt;
    private List<StorageBatchItemResponse> items;
}
