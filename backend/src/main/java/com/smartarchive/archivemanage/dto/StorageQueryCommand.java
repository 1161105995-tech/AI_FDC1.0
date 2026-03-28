package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class StorageQueryCommand {
    private String sourceBindBatchCode;
    private String keyword;
}
