package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archivemanage.domain.AiModelConfig;
import com.smartarchive.archivemanage.dto.AiModelConfigCommand;
import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.AiModelConnectionTestResult;
import com.smartarchive.archivemanage.mapper.AiModelConfigMapper;
import com.smartarchive.archivemanage.service.AiModelConfigService;
import com.smartarchive.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AiModelConfigServiceImpl implements AiModelConfigService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private final AiModelConfigMapper aiModelConfigMapper;

    @Override
    public List<AiModelConfigResponse> list(String keyword) {
        return aiModelConfigMapper.selectList(new LambdaQueryWrapper<AiModelConfig>()
                .eq(AiModelConfig::getDeleteFlag, "N")
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                    .like(AiModelConfig::getModelCode, keyword.trim())
                    .or().like(AiModelConfig::getModelName, keyword.trim())
                    .or().like(AiModelConfig::getModelIdentifier, keyword.trim()))
                .orderByAsc(AiModelConfig::getModelType)
                .orderByAsc(AiModelConfig::getModelCode))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public AiModelConfigResponse detail(Long modelConfigId) {
        return toResponse(requireModel(modelConfigId));
    }

    @Override
    @Transactional
    public AiModelConfigResponse save(Long modelConfigId, AiModelConfigCommand command) {
        validateCommand(command);
        AiModelConfig entity = modelConfigId == null ? new AiModelConfig() : requireModel(modelConfigId);
        ensureCodeAvailable(command.getModelCode(), modelConfigId);
        entity.setModelCode(command.getModelCode().trim());
        entity.setModelName(command.getModelName().trim());
        entity.setModelType(command.getModelType().trim().toUpperCase());
        entity.setApiUrl(command.getApiUrl().trim());
        entity.setApiKey(command.getApiKey().trim());
        entity.setModelIdentifier(command.getModelIdentifier().trim());
        entity.setPromptTemplate(trimToNull(command.getPromptTemplate()));
        entity.setEmbeddingDimension(command.getEmbeddingDimension());
        entity.setTimeoutSeconds(command.getTimeoutSeconds());
        entity.setTopK(command.getTopK());
        entity.setScoreThreshold(command.getScoreThreshold());
        entity.setOfficialResultCount(command.getOfficialResultCount());
        entity.setOfficialScoreThreshold(command.getOfficialScoreThreshold());
        entity.setRelatedResultCount(command.getRelatedResultCount());
        entity.setRelatedScoreThreshold(command.getRelatedScoreThreshold());
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag()));
        entity.setRemark(trimToNull(command.getRemark()));
        entity.setDeleteFlag("N");
        if (modelConfigId == null) {
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            aiModelConfigMapper.insert(entity);
        } else {
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            aiModelConfigMapper.updateById(entity);
        }
        return toResponse(entity);
    }

    @Override
    public AiModelConnectionTestResult testConnection(AiModelConfigCommand command) {
        validateCommand(command);
        boolean reachableShape = command.getApiUrl().startsWith("http://") || command.getApiUrl().startsWith("https://") || command.getApiUrl().startsWith("local://");
        return AiModelConnectionTestResult.builder()
            .status(reachableShape ? "SUCCESS" : "FAILED")
            .message(reachableShape ? "模型配置格式校验通过，当前环境下已模拟连通性测试成功。" : "接口地址格式不正确，需以 http://、https:// 或 local:// 开头。")
            .build();
    }

    @Override
    public AiModelConnectionTestResult testConnection(Long modelConfigId) {
        AiModelConfig model = requireModel(modelConfigId);
        AiModelConfigCommand command = new AiModelConfigCommand();
        command.setModelCode(model.getModelCode());
        command.setModelName(model.getModelName());
        command.setModelType(model.getModelType());
        command.setApiUrl(model.getApiUrl());
        command.setApiKey(model.getApiKey());
        command.setModelIdentifier(model.getModelIdentifier());
        command.setPromptTemplate(model.getPromptTemplate());
        command.setEmbeddingDimension(model.getEmbeddingDimension());
        command.setTimeoutSeconds(model.getTimeoutSeconds());
        command.setTopK(model.getTopK());
        command.setScoreThreshold(model.getScoreThreshold());
        command.setOfficialResultCount(model.getOfficialResultCount());
        command.setOfficialScoreThreshold(model.getOfficialScoreThreshold());
        command.setRelatedResultCount(model.getRelatedResultCount());
        command.setRelatedScoreThreshold(model.getRelatedScoreThreshold());
        command.setEnabledFlag(model.getEnabledFlag());
        command.setRemark(model.getRemark());
        return testConnection(command);
    }

    @Override
    @Transactional
    public AiModelConfigResponse activate(Long modelConfigId) {
        AiModelConfig model = requireModel(modelConfigId);
        model.setEnabledFlag("Y");
        model.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        model.setLastUpdateDate(LocalDateTime.now());
        aiModelConfigMapper.updateById(model);
        return toResponse(model);
    }

    @Override
    @Transactional
    public AiModelConfigResponse deactivate(Long modelConfigId) {
        AiModelConfig model = requireModel(modelConfigId);
        model.setEnabledFlag("N");
        model.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        model.setLastUpdateDate(LocalDateTime.now());
        aiModelConfigMapper.updateById(model);
        return toResponse(model);
    }

    private AiModelConfig requireModel(Long modelConfigId) {
        AiModelConfig entity = aiModelConfigMapper.selectOne(new LambdaQueryWrapper<AiModelConfig>()
            .eq(AiModelConfig::getModelConfigId, modelConfigId)
            .eq(AiModelConfig::getDeleteFlag, "N")
            .last("limit 1"));
        if (entity == null) {
            throw new BusinessException("AI model config does not exist");
        }
        return entity;
    }

    private void ensureCodeAvailable(String modelCode, Long ignoreId) {
        AiModelConfig existing = aiModelConfigMapper.selectOne(new LambdaQueryWrapper<AiModelConfig>()
            .eq(AiModelConfig::getModelCode, modelCode.trim())
            .eq(AiModelConfig::getDeleteFlag, "N")
            .last("limit 1"));
        if (existing != null && !existing.getModelConfigId().equals(ignoreId)) {
            throw new BusinessException("Model code already exists");
        }
    }

    private void validateCommand(AiModelConfigCommand command) {
        if (!List.of("CHAT", "EMBEDDING", "RERANK").contains(command.getModelType().trim().toUpperCase())) {
            throw new BusinessException("modelType only supports CHAT, EMBEDDING, RERANK");
        }
        if (command.getEmbeddingDimension() == null || command.getEmbeddingDimension() <= 0) throw new BusinessException("embeddingDimension must be positive");
        if (command.getTimeoutSeconds() == null || command.getTimeoutSeconds() <= 0) throw new BusinessException("timeoutSeconds must be positive");
        if (command.getTopK() == null || command.getTopK() <= 0) throw new BusinessException("topK must be positive");
        if (command.getOfficialResultCount() == null || command.getOfficialResultCount() <= 0) throw new BusinessException("officialResultCount must be positive");
        if (command.getRelatedResultCount() == null || command.getRelatedResultCount() <= 0) throw new BusinessException("relatedResultCount must be positive");
        normalizeFlag(command.getEnabledFlag());
    }

    private String normalizeFlag(String flag) {
        String normalized = StringUtils.hasText(flag) ? flag.trim().toUpperCase() : "Y";
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException("enabledFlag only supports Y or N");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private AiModelConfigResponse toResponse(AiModelConfig item) {
        return AiModelConfigResponse.builder()
            .modelConfigId(item.getModelConfigId())
            .modelCode(item.getModelCode())
            .modelName(item.getModelName())
            .modelType(item.getModelType())
            .apiUrl(item.getApiUrl())
            .apiKey(item.getApiKey())
            .modelIdentifier(item.getModelIdentifier())
            .promptTemplate(item.getPromptTemplate())
            .embeddingDimension(item.getEmbeddingDimension())
            .timeoutSeconds(item.getTimeoutSeconds())
            .topK(item.getTopK())
            .scoreThreshold(item.getScoreThreshold())
            .officialResultCount(item.getOfficialResultCount())
            .officialScoreThreshold(item.getOfficialScoreThreshold())
            .relatedResultCount(item.getRelatedResultCount())
            .relatedScoreThreshold(item.getRelatedScoreThreshold())
            .enabledFlag(item.getEnabledFlag())
            .remark(item.getRemark())
            .build();
    }
}