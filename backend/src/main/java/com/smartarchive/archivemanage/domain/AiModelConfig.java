package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("ai_model_config")
public class AiModelConfig {
    @TableId(type = IdType.AUTO)
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
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}