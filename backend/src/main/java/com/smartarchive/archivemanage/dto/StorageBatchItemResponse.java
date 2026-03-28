package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageBatchItemResponse {
    private Long storageItemId;
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
