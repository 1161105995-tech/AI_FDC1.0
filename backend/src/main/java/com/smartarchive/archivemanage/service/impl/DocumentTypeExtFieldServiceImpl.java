package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.archivemanage.domain.ArchiveExtFieldConfig;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldCreateCommand;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldUpdateCommand;
import com.smartarchive.archivemanage.mapper.ArchiveExtFieldConfigMapper;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.documenttype.domain.DocumentType;
import com.smartarchive.documenttype.mapper.DocumentTypeMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentTypeExtFieldServiceImpl implements DocumentTypeExtFieldService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private final ArchiveExtFieldConfigMapper archiveExtFieldConfigMapper;
    private final DocumentTypeMapper documentTypeMapper;

    @Override
    public List<DocumentTypeExtFieldResponse> listDirect(String documentTypeCode) {
        DocumentType documentType = requireDocumentType(documentTypeCode);
        return archiveExtFieldConfigMapper.selectList(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
                .eq(ArchiveExtFieldConfig::getDocumentTypeCode, documentType.getTypeCode())
                .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
                .orderByAsc(ArchiveExtFieldConfig::getFormSortOrder)
                .orderByAsc(ArchiveExtFieldConfig::getFieldCode))
            .stream()
            .map(item -> toResponse(item, documentType.getLevelNum(), documentType.getTypeCode()))
            .toList();
    }

    @Override
    public List<DocumentTypeExtFieldResponse> listEffective(String documentTypeCode) {
        DocumentType current = requireDocumentType(documentTypeCode);
        List<String> typeCodes = new ArrayList<>();
        if (StringUtils.hasText(current.getAncestorPath())) {
            typeCodes.addAll(List.of(current.getAncestorPath().split("/")));
        }
        typeCodes.add(current.getTypeCode());
        Map<String, DocumentType> typeMap = documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>()
                .in(DocumentType::getTypeCode, typeCodes)
                .eq(DocumentType::getDeleteFlag, "N"))
            .stream()
            .collect(Collectors.toMap(DocumentType::getTypeCode, Function.identity(), (left, right) -> left));

        return archiveExtFieldConfigMapper.selectList(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
                .in(ArchiveExtFieldConfig::getDocumentTypeCode, typeCodes)
                .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
                .eq(ArchiveExtFieldConfig::getEnabledFlag, "Y")
                .orderByAsc(ArchiveExtFieldConfig::getFormSortOrder)
                .orderByAsc(ArchiveExtFieldConfig::getFieldCode))
            .stream()
            .sorted(Comparator.comparing((ArchiveExtFieldConfig item) -> typeMap.get(item.getDocumentTypeCode()).getLevelNum())
                .thenComparing(ArchiveExtFieldConfig::getFormSortOrder)
                .thenComparing(ArchiveExtFieldConfig::getFieldCode))
            .map(item -> {
                DocumentType source = typeMap.get(item.getDocumentTypeCode());
                return toResponse(item, source == null ? null : source.getLevelNum(), item.getDocumentTypeCode());
            })
            .toList();
    }

    @Override
    @Transactional
    public DocumentTypeExtFieldResponse create(String documentTypeCode, DocumentTypeExtFieldCreateCommand command) {
        DocumentType documentType = requireDocumentType(documentTypeCode);
        validateCommand(command.getFieldType(), command.getRequiredFlag(), command.getEnabledFlag(), command.getQueryEnabledFlag(), command.getDictCategoryCode());

        ArchiveExtFieldConfig entity = new ArchiveExtFieldConfig();
        entity.setFieldCode(generateFieldCode(documentType.getTypeCode()));
        entity.setDocumentTypeCode(documentType.getTypeCode());
        entity.setFieldName(command.getFieldName().trim());
        entity.setFieldType(command.getFieldType().trim().toUpperCase());
        entity.setDictCategoryCode(trimToNull(command.getDictCategoryCode()));
        entity.setRequiredFlag(normalizeFlag(command.getRequiredFlag(), "N"));
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setFormSortOrder(command.getFormSortOrder() == null ? 1 : command.getFormSortOrder());
        entity.setQueryEnabledFlag(normalizeFlag(command.getQueryEnabledFlag(), "N"));
        entity.setQuerySortOrder(command.getQuerySortOrder() == null ? 1 : command.getQuerySortOrder());
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        archiveExtFieldConfigMapper.insert(entity);
        return toResponse(entity, documentType.getLevelNum(), documentType.getTypeCode());
    }

    @Override
    @Transactional
    public DocumentTypeExtFieldResponse update(String documentTypeCode, String fieldCode, DocumentTypeExtFieldUpdateCommand command) {
        DocumentType documentType = requireDocumentType(documentTypeCode);
        validateCommand(command.getFieldType(), command.getRequiredFlag(), command.getEnabledFlag(), command.getQueryEnabledFlag(), command.getDictCategoryCode());
        ArchiveExtFieldConfig entity = requireField(documentTypeCode, fieldCode);
        entity.setFieldName(command.getFieldName().trim());
        entity.setFieldType(command.getFieldType().trim().toUpperCase());
        entity.setDictCategoryCode(trimToNull(command.getDictCategoryCode()));
        entity.setRequiredFlag(normalizeFlag(command.getRequiredFlag(), "N"));
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setFormSortOrder(command.getFormSortOrder() == null ? 1 : command.getFormSortOrder());
        entity.setQueryEnabledFlag(normalizeFlag(command.getQueryEnabledFlag(), "N"));
        entity.setQuerySortOrder(command.getQuerySortOrder() == null ? 1 : command.getQuerySortOrder());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        archiveExtFieldConfigMapper.updateById(entity);
        return toResponse(entity, documentType.getLevelNum(), documentType.getTypeCode());
    }

    @Override
    @Transactional
    public void delete(String documentTypeCode, String fieldCode) {
        requireDocumentType(documentTypeCode);
        ArchiveExtFieldConfig entity = requireField(documentTypeCode, fieldCode);
        archiveExtFieldConfigMapper.update(null, new LambdaUpdateWrapper<ArchiveExtFieldConfig>()
            .eq(ArchiveExtFieldConfig::getFieldId, entity.getFieldId())
            .set(ArchiveExtFieldConfig::getDeleteFlag, "Y")
            .set(ArchiveExtFieldConfig::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
            .set(ArchiveExtFieldConfig::getLastUpdateDate, LocalDateTime.now()));
    }

    private DocumentType requireDocumentType(String documentTypeCode) {
        DocumentType documentType = documentTypeMapper.selectOne(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getTypeCode, documentTypeCode)
            .eq(DocumentType::getDeleteFlag, "N")
            .last("limit 1"));
        if (documentType == null) {
            throw new BusinessException("Document type does not exist");
        }
        return documentType;
    }

    private ArchiveExtFieldConfig requireField(String documentTypeCode, String fieldCode) {
        ArchiveExtFieldConfig field = archiveExtFieldConfigMapper.selectOne(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
            .eq(ArchiveExtFieldConfig::getDocumentTypeCode, documentTypeCode)
            .eq(ArchiveExtFieldConfig::getFieldCode, fieldCode)
            .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
            .last("limit 1"));
        if (field == null) {
            throw new BusinessException("Extension field does not exist");
        }
        return field;
    }

    private void validateCommand(String fieldType, String requiredFlag, String enabledFlag, String queryEnabledFlag, String dictCategoryCode) {
        String normalizedFieldType = fieldType == null ? "" : fieldType.trim().toUpperCase();
        if (!List.of("TEXT", "DICT").contains(normalizedFieldType)) {
            throw new BusinessException("fieldType only supports TEXT or DICT");
        }
        if ("DICT".equals(normalizedFieldType) && !StringUtils.hasText(dictCategoryCode)) {
            throw new BusinessException("dictCategoryCode is required when fieldType is DICT");
        }
        validateFlag(requiredFlag, "requiredFlag");
        validateFlag(enabledFlag, "enabledFlag");
        validateFlag(queryEnabledFlag, "queryEnabledFlag");
    }

    private void validateFlag(String flag, String fieldName) {
        String normalized = normalizeFlag(flag, null);
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException(fieldName + " only supports Y or N");
        }
    }

    private String normalizeFlag(String flag, String defaultValue) {
        if (!StringUtils.hasText(flag)) {
            return defaultValue;
        }
        return flag.trim().toUpperCase();
    }

    private String generateFieldCode(String documentTypeCode) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String prefix = documentTypeCode.replaceAll("[^A-Za-z0-9]", "");
        prefix = prefix.length() > 12 ? prefix.substring(0, 12) : prefix;
        return "EXT_" + prefix + "_" + suffix;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private DocumentTypeExtFieldResponse toResponse(ArchiveExtFieldConfig item, Integer sourceLevel, String sourceDocumentTypeCode) {
        return DocumentTypeExtFieldResponse.builder()
            .fieldId(item.getFieldId())
            .fieldCode(item.getFieldCode())
            .documentTypeCode(item.getDocumentTypeCode())
            .fieldName(item.getFieldName())
            .fieldType(item.getFieldType())
            .dictCategoryCode(item.getDictCategoryCode())
            .requiredFlag(item.getRequiredFlag())
            .enabledFlag(item.getEnabledFlag())
            .formSortOrder(item.getFormSortOrder())
            .queryEnabledFlag(item.getQueryEnabledFlag())
            .querySortOrder(item.getQuerySortOrder())
            .sourceLevel(sourceLevel)
            .sourceDocumentTypeCode(sourceDocumentTypeCode)
            .lastUpdateDate(item.getLastUpdateDate())
            .build();
    }
}
