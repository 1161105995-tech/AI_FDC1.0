package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveTransferResponse {
    private String businessKey;
    private String processInstanceId;
    private Long workflowInstanceId;
    private Integer archiveCount;
    private List<String> archiveFilingCodes;
}
