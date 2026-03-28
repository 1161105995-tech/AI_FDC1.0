package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class StorageCreateCommand {
    @NotBlank
    private String sourceType;
    private String sourceBindBatchCode;
    @NotBlank
    private String warehouseCode;
    private String remark;
    @NotEmpty
    private List<StorageCreateItemCommand> items;

    @Data
    public static class StorageCreateItemCommand {
        private String itemType;
        private Long volumeId;
        private Long archiveId;
        private String locationCode;
    }
}
