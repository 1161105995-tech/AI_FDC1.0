package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class StorageLedgerQueryCommand {
    private String storageBatchCode;
    private String bindVolumeCode;
    private String archiveCode;
    private String warehouseCode;
    private String locationCode;
    private String resultStatus;
}
