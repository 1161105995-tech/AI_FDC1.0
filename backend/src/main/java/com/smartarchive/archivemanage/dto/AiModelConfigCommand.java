package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiModelConfigCommand {
    @NotBlank
    private String modelCode;
    @NotBlank
    private String modelName;
    @NotBlank
    private String modelType;
    @NotBlank
    private String apiUrl;
    @NotBlank
    private String apiKey;
    @NotBlank
    private String modelIdentifier;
    private String promptTemplate;
    @NotNull
    private Integer embeddingDimension;
    @NotNull
    private Integer timeoutSeconds;
    @NotNull
    private Integer topK;
    private Double scoreThreshold;
    @NotNull
    private Integer officialResultCount;
    @NotNull
    private Double officialScoreThreshold;
    @NotNull
    private Integer relatedResultCount;
    @NotNull
    private Double relatedScoreThreshold;
    @NotBlank
    private String enabledFlag;
    private String remark;
}