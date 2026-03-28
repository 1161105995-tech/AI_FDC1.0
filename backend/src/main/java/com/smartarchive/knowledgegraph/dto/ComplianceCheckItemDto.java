package com.smartarchive.knowledgegraph.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplianceCheckItemDto {
    private String code;
    private String label;
    private String status;
    private String message;
}
