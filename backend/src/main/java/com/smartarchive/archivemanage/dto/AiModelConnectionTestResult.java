package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiModelConnectionTestResult {
    private String status;
    private String message;
}