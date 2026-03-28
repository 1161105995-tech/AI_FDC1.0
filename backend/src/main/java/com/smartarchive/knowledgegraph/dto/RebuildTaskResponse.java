package com.smartarchive.knowledgegraph.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RebuildTaskResponse {
    private Long taskId;
    private String taskCode;
    private String targetScope;
    private String targetValue;
    private String taskStatus;
    private String summary;
    private LocalDateTime lastUpdateDate;
}
