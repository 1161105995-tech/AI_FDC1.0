package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindVolumeItemResponse {
    private Long itemId;
    private Long archiveId;
    private String archiveCode;
    private String documentName;
    private Integer sortNo;
    private String primaryFlag;
    private String bindReason;
}
