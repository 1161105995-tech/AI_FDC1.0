package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageLedgerResponse {
    private Long ledgerId;
    private String ledgerCode;
    private String storageBatchCode;
    private String itemType;
    private String bindBatchCode;
    private String bindVolumeCode;
    private String archiveCode;
    private String warehouseCode;
    private String locationCode;
    private String actionType;
    private String resultStatus;
    private String operatorName;
    private LocalDateTime operationTime;
    private String operationSummary;
}
