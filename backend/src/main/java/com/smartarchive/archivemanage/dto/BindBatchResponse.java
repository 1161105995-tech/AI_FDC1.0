package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindBatchResponse {
    private Long bindBatchId;
    private String bindBatchCode;
    private String bindMode;
    private String bindStatus;
    private String bindRemark;
    private String guidedStorageFlag;
    private Integer volumeCount;
    private Integer archiveCount;
    private String nextAction;
    private LocalDateTime creationDate;
    private List<BindVolumeResponse> volumes;
}
