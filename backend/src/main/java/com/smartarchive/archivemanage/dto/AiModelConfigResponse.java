package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiModelConfigResponse {
    private Long modelConfigId;
    private String modelCode;
    private String modelName;
    private String modelType;
    private String apiUrl;
    private String apiKey;
    private String modelIdentifier;
    private String promptTemplate;
    private Integer embeddingDimension;
    private Integer timeoutSeconds;
    private Integer topK;
    private Double scoreThreshold;
    private Integer officialResultCount;
    private Double officialScoreThreshold;
    private Integer relatedResultCount;
    private Double relatedScoreThreshold;
    private String enabledFlag;
    private String remark;
}