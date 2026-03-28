package com.smartarchive.knowledgegraph.dto;

import lombok.Data;

@Data
public class RebuildTaskRequest {
    private String targetScope = "ALL";
    private String targetValue;
    private Integer requestedRuleVersion;
}
