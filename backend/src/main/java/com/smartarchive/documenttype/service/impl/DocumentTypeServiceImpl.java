package com.smartarchive.documenttype.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.common.audit.service.OperationAuditService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.documenttype.domain.DocumentType;
import com.smartarchive.documenttype.dto.DocumentTypeCreateCommand;
import com.smartarchive.documenttype.dto.DocumentTypePermissionPreviewResponse;
import com.smartarchive.documenttype.dto.DocumentTypeTreeNodeResponse;
import com.smartarchive.documenttype.dto.DocumentTypeUpdateCommand;
import com.smartarchive.documenttype.mapper.DocumentTypeMapper;
import com.smartarchive.documenttype.service.DocumentTypeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private static final int MAX_LEVEL = 5;
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final String SYSTEM_OPERATOR_NAME = "system";
    private static final String MODULE_CODE = "DOCUMENT_TYPE";
    private static final String MODULE_NAME = "Document Type Management";

    private final DocumentTypeMapper documentTypeMapper;
    private final OperationAuditService operationAuditService;

    @Override
    public List<DocumentTypeTreeNodeResponse> listTree() {
        List<DocumentType> items = listActiveTypes();
        Map<String, DocumentTypeTreeNodeResponse> nodeMap = items.stream()
            .map(this::toNode)
            .collect(Collectors.toMap(DocumentTypeTreeNodeResponse::getTypeCode, Function.identity(), (left, right) -> left));

        List<DocumentTypeTreeNodeResponse> roots = new ArrayList<>();
        for (DocumentType item : items) {
            DocumentTypeTreeNodeResponse current = nodeMap.get(item.getTypeCode());
            if (!StringUtils.hasText(item.getParentCode())) {
                roots.add(current);
                continue;
            }
            DocumentTypeTreeNodeResponse parent = nodeMap.get(item.getParentCode());
            if (parent == null) {
                roots.add(current);
                continue;
            }
            parent.getChildren().add(current);
        }
        sortTree(roots);
        return roots;
    }

    @Override
    public DocumentTypeTreeNodeResponse getDetail(String typeCode) {
        return toNode(findActiveByCode(typeCode));
    }

    @Override
    @Transactional
    public DocumentTypeTreeNodeResponse create(DocumentTypeCreateCommand command) {
        validateEnabledFlag(command.getEnabledFlag());
        ensureCodeAvailable(command.getTypeCode(), null);
        TreeMeta meta = resolveTreeMeta(command.getParentCode(), null);

        DocumentType entity = new DocumentType();
        entity.setTypeCode(command.getTypeCode().trim());
        entity.setTypeName(command.getTypeName().trim());
        entity.setDescription(trimToNull(command.getDescription()));
        entity.setEnabledFlag(command.getEnabledFlag().trim());
        entity.setParentCode(trimToNull(command.getParentCode()));
        entity.setLevelNum(meta.levelNum());
        entity.setAncestorPath(meta.ancestorPath());
        entity.setSortOrder(nextSortOrder(command.getParentCode()));
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        documentTypeMapper.insert(entity);

        DocumentTypeTreeNodeResponse response = toNode(entity);
        operationAuditService.record(
            MODULE_CODE,
            MODULE_NAME,
            "DOCUMENT_TYPE",
            entity.getTypeCode(),
            "CREATE",
            "Create document type",
            null,
            response,
            SYSTEM_OPERATOR_ID,
            SYSTEM_OPERATOR_NAME
        );
        return response;
    }

    @Override
    @Transactional
    public DocumentTypeTreeNodeResponse update(String typeCode, DocumentTypeUpdateCommand command) {
        validateEnabledFlag(command.getEnabledFlag());
        DocumentType existing = findActiveByCode(typeCode);
        DocumentTypeTreeNodeResponse before = toNode(existing);
        TreeMeta meta = resolveTreeMeta(command.getParentCode(), existing.getTypeCode());

        existing.setTypeName(command.getTypeName().trim());
        existing.setDescription(trimToNull(command.getDescription()));
        existing.setEnabledFlag(command.getEnabledFlag().trim());
        existing.setParentCode(trimToNull(command.getParentCode()));
        existing.setLevelNum(meta.levelNum());
        existing.setAncestorPath(meta.ancestorPath());
        existing.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        existing.setLastUpdateDate(LocalDateTime.now());
        documentTypeMapper.updateById(existing);

        if (hasChildren(existing.getTypeCode())) {
            refreshDescendants(existing);
        }

        DocumentType refreshed = findActiveByCode(typeCode);
        DocumentTypeTreeNodeResponse after = toNode(refreshed);
        operationAuditService.record(
            MODULE_CODE,
            MODULE_NAME,
            "DOCUMENT_TYPE",
            refreshed.getTypeCode(),
            "UPDATE",
            "Update document type",
            before,
            after,
            SYSTEM_OPERATOR_ID,
            SYSTEM_OPERATOR_NAME
        );
        return after;
    }

    @Override
    @Transactional
    public void delete(String typeCode) {
        DocumentType existing = findActiveByCode(typeCode);
        if (hasChildren(typeCode)) {
            throw new BusinessException("Current document type has child nodes and cannot be deleted directly");
        }

        DocumentTypeTreeNodeResponse before = toNode(existing);
        documentTypeMapper.update(null, new LambdaUpdateWrapper<DocumentType>()
            .eq(DocumentType::getId, existing.getId())
            .eq(DocumentType::getDeleteFlag, "N")
            .set(DocumentType::getDeleteFlag, "Y")
            .set(DocumentType::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
            .set(DocumentType::getLastUpdateDate, LocalDateTime.now()));

        operationAuditService.record(
            MODULE_CODE,
            MODULE_NAME,
            "DOCUMENT_TYPE",
            existing.getTypeCode(),
            "DELETE",
            "Soft delete document type",
            before,
            null,
            SYSTEM_OPERATOR_ID,
            SYSTEM_OPERATOR_NAME
        );
    }

    @Override
    public DocumentTypePermissionPreviewResponse getPermissionPreview() {
        return DocumentTypePermissionPreviewResponse.builder()
            .moduleCode(MODULE_CODE)
            .moduleName(MODULE_NAME)
            .permissionPoints(List.of(
                DocumentTypePermissionPreviewResponse.PermissionPoint.builder()
                    .code("document-type:create")
                    .name("Document Type Create")
                    .action("CREATE")
                    .description("Allow creating document type nodes")
                    .build(),
                DocumentTypePermissionPreviewResponse.PermissionPoint.builder()
                    .code("document-type:edit")
                    .name("Document Type Edit")
                    .action("UPDATE")
                    .description("Allow editing document type name, description, parent, and enabled status")
                    .build(),
                DocumentTypePermissionPreviewResponse.PermissionPoint.builder()
                    .code("document-type:view")
                    .name("Document Type View")
                    .action("READ")
                    .description("Allow viewing document type tree and details")
                    .build()
            ))
            .dataDimensions(List.of(
                DocumentTypePermissionPreviewResponse.DataDimension.builder()
                    .code("ALL")
                    .name("All Document Types")
                    .description("Can view full document type tree up to 5 levels")
                    .build(),
                DocumentTypePermissionPreviewResponse.DataDimension.builder()
                    .code("ROOT_BRANCH")
                    .name("Root Branch")
                    .description("Authorize by top-level branch")
                    .build(),
                DocumentTypePermissionPreviewResponse.DataDimension.builder()
                    .code("ENABLED_ONLY")
                    .name("Enabled Only")
                    .description("Only allow enabled document type data")
                    .build()
            ))
            .build();
    }

    private List<DocumentType> listActiveTypes() {
        return documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getDeleteFlag, "N")
            .orderByAsc(DocumentType::getLevelNum)
            .orderByAsc(DocumentType::getSortOrder)
            .orderByAsc(DocumentType::getTypeCode));
    }

    private DocumentType findActiveByCode(String typeCode) {
        DocumentType item = documentTypeMapper.selectOne(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getTypeCode, typeCode)
            .eq(DocumentType::getDeleteFlag, "N")
            .last("limit 1"));
        if (item == null) {
            throw new BusinessException("Document type does not exist");
        }
        return item;
    }

    private void ensureCodeAvailable(String typeCode, Long ignoreId) {
        DocumentType existing = documentTypeMapper.selectOne(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getTypeCode, typeCode.trim())
            .eq(DocumentType::getDeleteFlag, "N")
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), ignoreId)) {
            throw new BusinessException("Document type code already exists");
        }
    }

    private TreeMeta resolveTreeMeta(String parentCode, String currentTypeCode) {
        if (!StringUtils.hasText(parentCode)) {
            return new TreeMeta(1, null);
        }
        DocumentType parent = findActiveByCode(parentCode.trim());
        if (StringUtils.hasText(currentTypeCode) && currentTypeCode.equals(parent.getTypeCode())) {
            throw new BusinessException("Parent type cannot be the current node itself");
        }
        if (StringUtils.hasText(currentTypeCode) && isDescendant(parent, currentTypeCode)) {
            throw new BusinessException("Parent type cannot be selected from descendants of current node");
        }

        int level = parent.getLevelNum() + 1;
        if (level > MAX_LEVEL) {
            throw new BusinessException("Document type tree supports up to 5 levels");
        }

        String ancestorPath = StringUtils.hasText(parent.getAncestorPath())
            ? parent.getAncestorPath() + "/" + parent.getTypeCode()
            : parent.getTypeCode();
        return new TreeMeta(level, ancestorPath);
    }

    private boolean isDescendant(DocumentType candidateParent, String currentTypeCode) {
        String path = candidateParent.getAncestorPath();
        if (!StringUtils.hasText(path)) {
            return false;
        }
        List<String> ancestors = List.of(path.split("/"));
        return ancestors.contains(currentTypeCode);
    }

    private int nextSortOrder(String parentCode) {
        Long count = documentTypeMapper.selectCount(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getDeleteFlag, "N")
            .eq(StringUtils.hasText(parentCode), DocumentType::getParentCode, parentCode)
            .isNull(!StringUtils.hasText(parentCode), DocumentType::getParentCode));
        return count.intValue() + 1;
    }

    private boolean hasChildren(String typeCode) {
        return documentTypeMapper.selectCount(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getParentCode, typeCode)
            .eq(DocumentType::getDeleteFlag, "N")) > 0;
    }

    private void refreshDescendants(DocumentType parent) {
        List<DocumentType> children = documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getParentCode, parent.getTypeCode())
            .eq(DocumentType::getDeleteFlag, "N"));

        for (DocumentType child : children) {
            child.setLevelNum(parent.getLevelNum() + 1);
            if (child.getLevelNum() > MAX_LEVEL) {
                throw new BusinessException("Moving parent would exceed max level 5");
            }
            child.setAncestorPath(StringUtils.hasText(parent.getAncestorPath())
                ? parent.getAncestorPath() + "/" + parent.getTypeCode()
                : parent.getTypeCode());
            child.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            child.setLastUpdateDate(LocalDateTime.now());
            documentTypeMapper.updateById(child);
            refreshDescendants(child);
        }
    }

    private void validateEnabledFlag(String enabledFlag) {
        if (!"Y".equals(enabledFlag) && !"N".equals(enabledFlag)) {
            throw new BusinessException("enabledFlag only supports Y or N");
        }
    }

    private DocumentTypeTreeNodeResponse toNode(DocumentType item) {
        DocumentTypeTreeNodeResponse node = new DocumentTypeTreeNodeResponse();
        node.setId(item.getId());
        node.setTypeCode(item.getTypeCode());
        node.setTypeName(item.getTypeName());
        node.setDescription(item.getDescription());
        node.setEnabledFlag(item.getEnabledFlag());
        node.setParentCode(item.getParentCode());
        node.setLevelNum(item.getLevelNum());
        node.setAncestorPath(item.getAncestorPath());
        node.setSortOrder(item.getSortOrder());
        node.setDeleteFlag(item.getDeleteFlag());
        node.setCreatedBy(item.getCreatedBy());
        node.setCreationDate(item.getCreationDate());
        node.setLastUpdatedBy(item.getLastUpdatedBy());
        node.setLastUpdateDate(item.getLastUpdateDate());
        return node;
    }

    private void sortTree(List<DocumentTypeTreeNodeResponse> nodes) {
        nodes.sort(Comparator.comparing(DocumentTypeTreeNodeResponse::getSortOrder, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(DocumentTypeTreeNodeResponse::getTypeCode));
        for (DocumentTypeTreeNodeResponse node : nodes) {
            sortTree(node.getChildren());
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private record TreeMeta(int levelNum, String ancestorPath) {
    }
}
