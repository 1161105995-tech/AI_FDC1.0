package com.smartarchive.knowledgegraph.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphEdgeDto {
    private String edgeKey;
    private String edgeType;
    private String fromNodeKey;
    private String toNodeKey;
    private String edgeStatus;
    private Double confidence;
    private Long sourceArchiveId;
    private String sourceFieldCode;
    private String sourceSemanticCode;
    private String matchRuleCode;
    private Integer ruleVersion;
    private Map<String, Object> attributes;
}
