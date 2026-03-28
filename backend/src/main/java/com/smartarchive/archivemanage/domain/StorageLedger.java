package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_storage_ledger")
public class StorageLedger {
    @TableId(type = IdType.AUTO)
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
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operationTime;
    private String operationSummary;
}
