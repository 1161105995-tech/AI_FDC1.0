package com.smartarchive.archiveflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archiveflow.domain.ArchiveFlowRule;
import com.smartarchive.archiveflow.domain.SecurityLevelDictionary;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleCreateCommand;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleDetailResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleOptionResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRulePermissionPreviewResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleSummaryResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleUpdateCommand;
import com.smartarchive.archiveflow.dto.SecurityLevelOptionResponse;
import com.smartarchive.archiveflow.mapper.ArchiveFlowLookupMapper;
import com.smartarchive.archiveflow.mapper.ArchiveFlowRuleMapper;
import com.smartarchive.archiveflow.mapper.SecurityLevelDictionaryMapper;
import com.smartarchive.archiveflow.service.ArchiveFlowRuleService;
import com.smartarchive.common.audit.service.OperationAuditService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyproject.domain.CompanyProject;
import com.smartarchive.companyproject.mapper.CompanyProjectMapper;
import com.smartarchive.documentorganization.domain.DocumentOrganizationCity;
import com.smartarchive.documentorganization.mapper.DocumentOrganizationCityMapper;
import com.smartarchive.documenttype.domain.DocumentType;
import com.smartarchive.documenttype.mapper.DocumentTypeMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ArchiveFlowRuleServiceImpl implements ArchiveFlowRuleService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final String SYSTEM_OPERATOR_NAME = "system";
    private static final String MODULE_CODE = "ARCHIVE_FLOW_RULE";
    private static final String MODULE_NAME = "Archive Flow Rule Management";

    private final ArchiveFlowRuleMapper archiveFlowRuleMapper;
    private final SecurityLevelDictionaryMapper securityLevelDictionaryMapper;
    private final ArchiveFlowLookupMapper archiveFlowLookupMapper;
    private final CompanyProjectMapper companyProjectMapper;
    private final DocumentTypeMapper documentTypeMapper;
    private final DocumentOrganizationCityMapper documentOrganizationCityMapper;
    private final OperationAuditService operationAuditService;

    @Override
    public List<ArchiveFlowRuleSummaryResponse> list(String keyword,
                                                     String companyProjectCode,
                                                     String documentTypeCode,
                                                     String documentOrganizationCode,
                                                     String enabledFlag) {
        Map<String, String> companyProjectNameMap = listActiveCompanyProjects().stream()
            .collect(Collectors.toMap(CompanyProject::getCompanyProjectCode, CompanyProject::getCompanyProjectName, (left, right) -> left));
        Map<String, String> documentTypeNameMap = listActiveDocumentTypes().stream()
            .collect(Collectors.toMap(DocumentType::getTypeCode, DocumentType::getTypeName, (left, right) -> left));
        Map<String, String> documentOrganizationNameMap = listActiveDocumentOrganizationCodes().stream()
            .collect(Collectors.toMap(Function.identity(), Function.identity(), (left, right) -> left));
        Map<String, String> cityNameMap = listActiveCities().stream()
            .collect(Collectors.toMap(DocumentOrganizationCity::getCityCode, DocumentOrganizationCity::getCityName, (left, right) -> left));
        Map<String, String> securityLevelNameMap = listActiveSecurityLevels().stream()
            .collect(Collectors.toMap(SecurityLevelDictionary::getSecurityLevelCode, SecurityLevelDictionary::getSecurityLevelName, (left, right) -> left));

        return archiveFlowRuleMapper.selectList(new LambdaQueryWrapper<ArchiveFlowRule>()
                .eq(ArchiveFlowRule::getDeleteFlag, "N")
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                    .like(ArchiveFlowRule::getCompanyProjectCode, keyword.trim())
                    .or()
                    .like(ArchiveFlowRule::getDocumentTypeCode, keyword.trim())
                    .or()
                    .like(ArchiveFlowRule::getDocumentOrganizationCode, keyword.trim()))
                .eq(StringUtils.hasText(companyProjectCode), ArchiveFlowRule::getCompanyProjectCode, trimToNull(companyProjectCode))
                .eq(StringUtils.hasText(documentTypeCode), ArchiveFlowRule::getDocumentTypeCode, trimToNull(documentTypeCode))
                .eq(StringUtils.hasText(documentOrganizationCode), ArchiveFlowRule::getDocumentOrganizationCode, trimToNull(documentOrganizationCode))
                .eq(StringUtils.hasText(enabledFlag), ArchiveFlowRule::getEnabledFlag, trimToNull(enabledFlag))
                .orderByDesc(ArchiveFlowRule::getLastUpdateDate)
                .orderByAsc(ArchiveFlowRule::getCompanyProjectCode))
            .stream()
            .map(item -> toSummary(item, companyProjectNameMap, documentTypeNameMap, documentOrganizationNameMap, cityNameMap, securityLevelNameMap))
            .toList();
    }

    @Override
    public ArchiveFlowRuleDetailResponse getDetail(String companyProjectCode) {
        return toDetail(findActiveByCompanyProjectCode(requireText(companyProjectCode, "companyProjectCode")));
    }

    @Override
    @Transactional
    public ArchiveFlowRuleDetailResponse create(ArchiveFlowRuleCreateCommand command) {
        String companyProjectCode = requireText(command.getCompanyProjectCode(), "companyProjectCode");
        ensureCompanyProjectAvailable(companyProjectCode);
        ensureBusinessKeyAvailable(companyProjectCode, null);

        String documentTypeCode = requireText(command.getDocumentTypeCode(), "documentTypeCode");
        String documentOrganizationCode = requireText(command.getDocumentOrganizationCode(), "documentOrganizationCode");
        String securityLevelCode = requireText(command.getSecurityLevelCode(), "securityLevelCode");
        String archiveDestination = trimToNull(command.getArchiveDestination());
        validateType(documentTypeCode);
        validateDocumentOrganization(documentOrganizationCode);
        validateCity(archiveDestination);
        validateSecurityLevel(securityLevelCode);
        validateFlag(requireText(command.getExternalDisplayFlag(), "externalDisplayFlag"), "externalDisplayFlag");
        validateFlag(requireText(command.getEnabledFlag(), "enabledFlag"), "enabledFlag");
        validateRetentionPeriodYears(command.getRetentionPeriodYears());

        LocalDateTime now = LocalDateTime.now();
        ArchiveFlowRule entity = new ArchiveFlowRule();
        entity.setCompanyProjectCode(companyProjectCode);
        entity.setDocumentTypeCode(documentTypeCode);
        entity.setCustomRule(trimToNull(command.getCustomRule()));
        entity.setArchiveDestination(archiveDestination);
        entity.setDocumentOrganizationCode(documentOrganizationCode);
        entity.setRetentionPeriodYears(command.getRetentionPeriodYears());
        entity.setSecurityLevelCode(securityLevelCode);
        entity.setExternalDisplayFlag(command.getExternalDisplayFlag().trim());
        entity.setEnabledFlag(command.getEnabledFlag().trim());
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(now);
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(now);
        archiveFlowRuleMapper.insert(entity);

        ArchiveFlowRuleDetailResponse after = toDetail(entity);
        operationAuditService.record(MODULE_CODE, MODULE_NAME, "ARCHIVE_FLOW_RULE", companyProjectCode, "CREATE", "Create archive flow rule", null, after, SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
        return after;
    }

    @Override
    @Transactional
    public ArchiveFlowRuleDetailResponse update(String companyProjectCode, ArchiveFlowRuleUpdateCommand command) {
        ArchiveFlowRule existing = findActiveByCompanyProjectCode(requireText(companyProjectCode, "companyProjectCode"));
        ArchiveFlowRuleDetailResponse before = toDetail(existing);

        String documentTypeCode = requireText(command.getDocumentTypeCode(), "documentTypeCode");
        String documentOrganizationCode = requireText(command.getDocumentOrganizationCode(), "documentOrganizationCode");
        String securityLevelCode = requireText(command.getSecurityLevelCode(), "securityLevelCode");
        String archiveDestination = trimToNull(command.getArchiveDestination());
        validateType(documentTypeCode);
        validateDocumentOrganization(documentOrganizationCode);
        validateCity(archiveDestination);
        validateSecurityLevel(securityLevelCode);
        validateFlag(requireText(command.getExternalDisplayFlag(), "externalDisplayFlag"), "externalDisplayFlag");
        validateFlag(requireText(command.getEnabledFlag(), "enabledFlag"), "enabledFlag");
        validateRetentionPeriodYears(command.getRetentionPeriodYears());

        existing.setDocumentTypeCode(documentTypeCode);
        existing.setCustomRule(trimToNull(command.getCustomRule()));
        existing.setArchiveDestination(archiveDestination);
        existing.setDocumentOrganizationCode(documentOrganizationCode);
        existing.setRetentionPeriodYears(command.getRetentionPeriodYears());
        existing.setSecurityLevelCode(securityLevelCode);
        existing.setExternalDisplayFlag(command.getExternalDisplayFlag().trim());
        existing.setEnabledFlag(command.getEnabledFlag().trim());
        existing.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        existing.setLastUpdateDate(LocalDateTime.now());
        archiveFlowRuleMapper.updateById(existing);

        ArchiveFlowRuleDetailResponse after = toDetail(existing);
        operationAuditService.record(MODULE_CODE, MODULE_NAME, "ARCHIVE_FLOW_RULE", existing.getCompanyProjectCode(), "UPDATE", "Update archive flow rule", before, after, SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
        return after;
    }

    @Override
    @Transactional
    public void delete(String companyProjectCode) {
        ArchiveFlowRule existing = findActiveByCompanyProjectCode(requireText(companyProjectCode, "companyProjectCode"));
        ArchiveFlowRuleDetailResponse before = toDetail(existing);
        existing.setDeleteFlag("Y");
        existing.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        existing.setLastUpdateDate(LocalDateTime.now());
        archiveFlowRuleMapper.updateById(existing);
        operationAuditService.record(MODULE_CODE, MODULE_NAME, "ARCHIVE_FLOW_RULE", existing.getCompanyProjectCode(), "DELETE", "Soft delete archive flow rule", before, null, SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
    }

    @Override
    public List<ArchiveFlowRuleOptionResponse> listCompanyProjectOptions() {
        return listActiveCompanyProjects().stream()
            .map(item -> ArchiveFlowRuleOptionResponse.builder().code(item.getCompanyProjectCode()).name(item.getCompanyProjectName()).build())
            .toList();
    }

    @Override
    public List<ArchiveFlowRuleOptionResponse> listDocumentTypeOptions() {
        return listActiveDocumentTypes().stream()
            .map(item -> ArchiveFlowRuleOptionResponse.builder().code(item.getTypeCode()).name(item.getTypeName()).build())
            .toList();
    }

    @Override
    public List<ArchiveFlowRuleOptionResponse> listDocumentOrganizationOptions() {
        return listActiveDocumentOrganizationCodes().stream()
            .map(code -> ArchiveFlowRuleOptionResponse.builder().code(code).name(code).build())
            .toList();
    }

    @Override
    public List<ArchiveFlowRuleOptionResponse> listCityOptions() {
        return listActiveCities().stream()
            .map(item -> ArchiveFlowRuleOptionResponse.builder().code(item.getCityCode()).name(item.getCityName()).build())
            .toList();
    }

    @Override
    public List<SecurityLevelOptionResponse> listSecurityLevels() {
        return listActiveSecurityLevels().stream()
            .map(item -> SecurityLevelOptionResponse.builder()
                .securityLevelCode(item.getSecurityLevelCode())
                .securityLevelName(item.getSecurityLevelName())
                .build())
            .toList();
    }

    @Override
    public ArchiveFlowRulePermissionPreviewResponse getPermissionPreview() {
        return ArchiveFlowRulePermissionPreviewResponse.builder()
            .moduleCode(MODULE_CODE)
            .moduleName(MODULE_NAME)
            .permissionPoints(List.of(
                ArchiveFlowRulePermissionPreviewResponse.PermissionPoint.builder().code("archive-flow-rule:create").name("Archive Flow Rule Create").action("CREATE").description("Allow create archive flow default rules").build(),
                ArchiveFlowRulePermissionPreviewResponse.PermissionPoint.builder().code("archive-flow-rule:edit").name("Archive Flow Rule Edit").action("UPDATE").description("Allow edit archive flow default rules").build(),
                ArchiveFlowRulePermissionPreviewResponse.PermissionPoint.builder().code("archive-flow-rule:view").name("Archive Flow Rule View").action("READ").description("Allow view archive flow default rules").build()
            ))
            .dataDimensions(List.of(
                ArchiveFlowRulePermissionPreviewResponse.DataDimension.builder().code("COMPANY_PROJECT").name("Company/Project").description("Authorize by company/project dimension").build(),
                ArchiveFlowRulePermissionPreviewResponse.DataDimension.builder().code("DOCUMENT_TYPE").name("Document Type").description("Authorize by document type dimension").build(),
                ArchiveFlowRulePermissionPreviewResponse.DataDimension.builder().code("DOCUMENT_ORGANIZATION").name("Document Organization").description("Authorize by document organization dimension").build()
            ))
            .build();
    }

    private List<CompanyProject> listActiveCompanyProjects() {
        return companyProjectMapper.selectList(new LambdaQueryWrapper<CompanyProject>()
            .eq(CompanyProject::getDeleteFlag, "N")
            .eq(CompanyProject::getEnabledFlag, "Y")
            .orderByAsc(CompanyProject::getCompanyProjectCode));
    }

    private List<DocumentType> listActiveDocumentTypes() {
        return documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getDeleteFlag, "N")
            .eq(DocumentType::getEnabledFlag, "Y")
            .orderByAsc(DocumentType::getLevelNum)
            .orderByAsc(DocumentType::getSortOrder)
            .orderByAsc(DocumentType::getTypeCode));
    }

    private List<String> listActiveDocumentOrganizationCodes() {
        return archiveFlowLookupMapper.selectEnabledDocumentOrganizationCodes();
    }

    private List<DocumentOrganizationCity> listActiveCities() {
        return documentOrganizationCityMapper.selectList(new LambdaQueryWrapper<DocumentOrganizationCity>()
            .eq(DocumentOrganizationCity::getDeleteFlag, "N")
            .eq(DocumentOrganizationCity::getEnabledFlag, "Y")
            .orderByAsc(DocumentOrganizationCity::getCountryCode)
            .orderByAsc(DocumentOrganizationCity::getSortOrder)
            .orderByAsc(DocumentOrganizationCity::getCityCode));
    }

    private List<SecurityLevelDictionary> listActiveSecurityLevels() {
        return securityLevelDictionaryMapper.selectList(new LambdaQueryWrapper<SecurityLevelDictionary>()
            .eq(SecurityLevelDictionary::getDeleteFlag, "N")
            .eq(SecurityLevelDictionary::getEnabledFlag, "Y")
            .orderByAsc(SecurityLevelDictionary::getSortOrder)
            .orderByAsc(SecurityLevelDictionary::getSecurityLevelCode));
    }

    private ArchiveFlowRule findActiveByCompanyProjectCode(String companyProjectCode) {
        ArchiveFlowRule item = archiveFlowRuleMapper.selectOne(new LambdaQueryWrapper<ArchiveFlowRule>()
            .eq(ArchiveFlowRule::getCompanyProjectCode, companyProjectCode)
            .eq(ArchiveFlowRule::getDeleteFlag, "N")
            .last("limit 1"));
        if (item == null) {
            throw new BusinessException("Archive flow rule does not exist");
        }
        return item;
    }

    private void ensureBusinessKeyAvailable(String companyProjectCode, Long ignoreId) {
        ArchiveFlowRule existing = archiveFlowRuleMapper.selectOne(new LambdaQueryWrapper<ArchiveFlowRule>()
            .eq(ArchiveFlowRule::getCompanyProjectCode, companyProjectCode)
            .eq(ArchiveFlowRule::getDeleteFlag, "N")
            .last("limit 1"));
        if (existing != null && !existing.getId().equals(ignoreId)) {
            throw new BusinessException("companyProjectCode already exists");
        }
    }

    private void ensureCompanyProjectAvailable(String companyProjectCode) {
        Long count = companyProjectMapper.selectCount(new LambdaQueryWrapper<CompanyProject>()
            .eq(CompanyProject::getCompanyProjectCode, companyProjectCode)
            .eq(CompanyProject::getDeleteFlag, "N")
            .eq(CompanyProject::getEnabledFlag, "Y"));
        if (count == null || count == 0) {
            throw new BusinessException("companyProjectCode must come from enabled company/project data");
        }
    }

    private void validateType(String documentTypeCode) {
        Long count = documentTypeMapper.selectCount(new LambdaQueryWrapper<DocumentType>()
            .eq(DocumentType::getTypeCode, documentTypeCode)
            .eq(DocumentType::getDeleteFlag, "N")
            .eq(DocumentType::getEnabledFlag, "Y"));
        if (count == null || count == 0) {
            throw new BusinessException("documentTypeCode must come from enabled document types");
        }
    }

    private void validateDocumentOrganization(String documentOrganizationCode) {
        Integer count = archiveFlowLookupMapper.countEnabledDocumentOrganizationCode(documentOrganizationCode);
        if (count == null || count == 0) {
            throw new BusinessException("documentOrganizationCode must come from enabled document organizations");
        }
    }

    private void validateCity(String archiveDestination) {
        if (!StringUtils.hasText(archiveDestination)) {
            return;
        }
        Long count = documentOrganizationCityMapper.selectCount(new LambdaQueryWrapper<DocumentOrganizationCity>()
            .eq(DocumentOrganizationCity::getCityCode, archiveDestination.trim())
            .eq(DocumentOrganizationCity::getDeleteFlag, "N")
            .eq(DocumentOrganizationCity::getEnabledFlag, "Y"));
        if (count == null || count == 0) {
            throw new BusinessException("archiveDestination must come from enabled city dictionary entries");
        }
    }

    private void validateSecurityLevel(String securityLevelCode) {
        Long count = securityLevelDictionaryMapper.selectCount(new LambdaQueryWrapper<SecurityLevelDictionary>()
            .eq(SecurityLevelDictionary::getSecurityLevelCode, securityLevelCode)
            .eq(SecurityLevelDictionary::getDeleteFlag, "N")
            .eq(SecurityLevelDictionary::getEnabledFlag, "Y"));
        if (count == null || count == 0) {
            throw new BusinessException("securityLevelCode must come from enabled dictionary entries");
        }
    }

    private void validateRetentionPeriodYears(Integer years) {
        if (years == null || years < 0) {
            throw new BusinessException("retentionPeriodYears must be a non-negative number");
        }
    }

    private void validateFlag(String flag, String fieldName) {
        if (!"Y".equals(flag) && !"N".equals(flag)) {
            throw new BusinessException(fieldName + " only supports Y or N");
        }
    }

    private ArchiveFlowRuleSummaryResponse toSummary(ArchiveFlowRule item,
                                                     Map<String, String> companyProjectNameMap,
                                                     Map<String, String> documentTypeNameMap,
                                                     Map<String, String> documentOrganizationNameMap,
                                                     Map<String, String> cityNameMap,
                                                     Map<String, String> securityLevelNameMap) {
        ArchiveFlowRuleSummaryResponse response = new ArchiveFlowRuleSummaryResponse();
        response.setId(item.getId());
        response.setCompanyProjectCode(item.getCompanyProjectCode());
        response.setCompanyProjectName(companyProjectNameMap.getOrDefault(item.getCompanyProjectCode(), item.getCompanyProjectCode()));
        response.setDocumentTypeCode(item.getDocumentTypeCode());
        response.setDocumentTypeName(documentTypeNameMap.getOrDefault(item.getDocumentTypeCode(), item.getDocumentTypeCode()));
        response.setCustomRule(item.getCustomRule());
        response.setArchiveDestination(item.getArchiveDestination());
        response.setArchiveDestinationName(cityNameMap.getOrDefault(item.getArchiveDestination(), item.getArchiveDestination()));
        response.setDocumentOrganizationCode(item.getDocumentOrganizationCode());
        response.setDocumentOrganizationName(documentOrganizationNameMap.getOrDefault(item.getDocumentOrganizationCode(), item.getDocumentOrganizationCode()));
        response.setRetentionPeriodYears(item.getRetentionPeriodYears());
        response.setSecurityLevelCode(item.getSecurityLevelCode());
        response.setSecurityLevelName(securityLevelNameMap.getOrDefault(item.getSecurityLevelCode(), item.getSecurityLevelCode()));
        response.setExternalDisplayFlag(item.getExternalDisplayFlag());
        response.setEnabledFlag(item.getEnabledFlag());
        response.setLastUpdatedBy(item.getLastUpdatedBy());
        response.setLastUpdateDate(item.getLastUpdateDate());
        return response;
    }

    private ArchiveFlowRuleDetailResponse toDetail(ArchiveFlowRule item) {
        ArchiveFlowRuleDetailResponse response = new ArchiveFlowRuleDetailResponse();
        response.setId(item.getId());
        response.setCompanyProjectCode(item.getCompanyProjectCode());
        response.setDocumentTypeCode(item.getDocumentTypeCode());
        response.setCustomRule(item.getCustomRule());
        response.setArchiveDestination(item.getArchiveDestination());
        response.setDocumentOrganizationCode(item.getDocumentOrganizationCode());
        response.setRetentionPeriodYears(item.getRetentionPeriodYears());
        response.setSecurityLevelCode(item.getSecurityLevelCode());
        response.setExternalDisplayFlag(item.getExternalDisplayFlag());
        response.setEnabledFlag(item.getEnabledFlag());
        response.setDeleteFlag(item.getDeleteFlag());
        response.setCreatedBy(item.getCreatedBy());
        response.setCreationDate(item.getCreationDate());
        response.setLastUpdatedBy(item.getLastUpdatedBy());
        response.setLastUpdateDate(item.getLastUpdateDate());
        return response;
    }

    private String requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
