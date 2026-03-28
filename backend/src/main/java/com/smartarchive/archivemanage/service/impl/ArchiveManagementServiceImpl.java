package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archivemanage.domain.AiModelConfig;
import com.smartarchive.archivemanage.domain.ArchiveAttachment;
import com.smartarchive.archivemanage.domain.ArchiveContent;
import com.smartarchive.archivemanage.domain.ArchiveContentChunk;
import com.smartarchive.archivemanage.domain.ArchiveCreateSession;
import com.smartarchive.archivemanage.domain.ArchiveExtValue;
import com.smartarchive.archivemanage.domain.ArchivePaper;
import com.smartarchive.archivemanage.domain.ArchiveRecord;
import com.smartarchive.archivemanage.domain.BindBatch;
import com.smartarchive.archivemanage.domain.BindVolume;
import com.smartarchive.archivemanage.domain.BindVolumeItem;
import com.smartarchive.archivemanage.domain.StorageBatch;
import com.smartarchive.archivemanage.domain.StorageBatchItem;
import com.smartarchive.archivemanage.domain.StorageLedger;
import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.ArchiveAiParseResult;
import com.smartarchive.archivemanage.dto.ArchiveAskCommand;
import com.smartarchive.archivemanage.dto.ArchiveAskResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentUpdateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateOptionsResponse;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionResponse;
import com.smartarchive.archivemanage.dto.ArchiveDefaultResolveResponse;
import com.smartarchive.archivemanage.dto.ArchiveQueryCommand;
import com.smartarchive.archivemanage.dto.ArchiveQueryResponse;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import com.smartarchive.archivemanage.dto.ArchiveTransferCommand;
import com.smartarchive.archivemanage.dto.ArchiveTransferResponse;
import com.smartarchive.archivemanage.dto.BindArchiveCandidateResponse;
import com.smartarchive.archivemanage.dto.BindBatchResponse;
import com.smartarchive.archivemanage.dto.BindCreateCommand;
import com.smartarchive.archivemanage.dto.BindOptionsResponse;
import com.smartarchive.archivemanage.dto.BindPreviewCommand;
import com.smartarchive.archivemanage.dto.BindPreviewResponse;
import com.smartarchive.archivemanage.dto.BindQueryCommand;
import com.smartarchive.archivemanage.dto.BindVolumeItemResponse;
import com.smartarchive.archivemanage.dto.BindVolumeResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.LabelValueOption;
import com.smartarchive.archivemanage.dto.StorageBatchItemResponse;
import com.smartarchive.archivemanage.dto.StorageBatchResponse;
import com.smartarchive.archivemanage.dto.StorageCreateCommand;
import com.smartarchive.archivemanage.dto.StorageLedgerQueryCommand;
import com.smartarchive.archivemanage.dto.StorageLedgerResponse;
import com.smartarchive.archivemanage.dto.StorageOptionsResponse;
import com.smartarchive.archivemanage.dto.StorageQueryCommand;
import com.smartarchive.archivemanage.dto.StorageQueryResponse;
import com.smartarchive.archivemanage.mapper.AiModelConfigMapper;
import com.smartarchive.archivemanage.mapper.ArchiveAttachmentMapper;
import com.smartarchive.archivemanage.mapper.ArchiveContentChunkMapper;
import com.smartarchive.archivemanage.mapper.ArchiveContentMapper;
import com.smartarchive.archivemanage.mapper.ArchiveCreateSessionMapper;
import com.smartarchive.archivemanage.mapper.ArchiveExtValueMapper;
import com.smartarchive.archivemanage.mapper.ArchivePaperMapper;
import com.smartarchive.archivemanage.mapper.ArchiveRecordMapper;
import com.smartarchive.archivemanage.mapper.BindBatchMapper;
import com.smartarchive.archivemanage.mapper.BindVolumeItemMapper;
import com.smartarchive.archivemanage.mapper.BindVolumeMapper;
import com.smartarchive.archivemanage.mapper.StorageBatchItemMapper;
import com.smartarchive.archivemanage.mapper.StorageBatchMapper;
import com.smartarchive.archivemanage.mapper.StorageLedgerMapper;
import com.smartarchive.archivemanage.service.ArchiveManagementService;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.archivemanage.service.support.ArchiveAiChatService;
import com.smartarchive.archivemanage.service.support.ArchiveFileTextExtractor;
import com.smartarchive.archivemanage.service.support.ArchiveTextChunkService;
import com.smartarchive.archivemanage.service.support.ArchiveTextVectorService;
import com.smartarchive.archive.domain.ArchiveReceipt;
import com.smartarchive.archive.mapper.ArchiveReceiptMapper;
import com.smartarchive.archiveflow.domain.ArchiveFlowRule;
import com.smartarchive.archiveflow.domain.SecurityLevelDictionary;
import com.smartarchive.archiveflow.mapper.ArchiveFlowRuleMapper;
import com.smartarchive.archiveflow.mapper.SecurityLevelDictionaryMapper;
import com.smartarchive.common.audit.service.OperationAuditService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyproject.domain.CompanyProject;
import com.smartarchive.companyproject.mapper.CompanyProjectMapper;
import com.smartarchive.documentorganization.domain.DocumentOrganization;
import com.smartarchive.documentorganization.domain.DocumentOrganizationCity;
import com.smartarchive.documentorganization.mapper.DocumentOrganizationCityMapper;
import com.smartarchive.documentorganization.mapper.DocumentOrganizationMapper;
import com.smartarchive.documenttype.domain.DocumentType;
import com.smartarchive.documenttype.mapper.DocumentTypeMapper;
import com.smartarchive.warehouse.domain.Warehouse;
import com.smartarchive.warehouse.domain.WarehouseLocation;
import com.smartarchive.warehouse.mapper.WarehouseLocationMapper;
import com.smartarchive.warehouse.mapper.WarehouseMapper;
import com.smartarchive.workflow.domain.WorkflowInstance;
import com.smartarchive.workflow.dto.StartProcessCommand;
import com.smartarchive.workflow.service.WorkflowService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveManagementServiceImpl implements ArchiveManagementService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final String SYSTEM_OPERATOR_NAME = "System";
    private static final Pattern BUSINESS_CODE_PATTERN = Pattern.compile("[A-Z]{2,}[A-Z0-9_-]{2,}");
    private static final Set<String> TEXT_EXTENSIONS = Set.of("txt", "md", "json", "csv", "xml", "sql", "log", "yml", "yaml", "properties", "java", "ts", "js", "vue", "html", "htm");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("(20\\d{2})[年\\-./](\\d{1,2})");
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("姓名[：:：]?\\s*([\\p{IsHan}A-Za-z·]{2,20})");

    private static final Pattern FILE_NAME_DATE_PATTERN = Pattern.compile("(20\\d{2})(\\d{2})(\\d{2})");
    private static final Pattern COMPANY_IN_BRACKETS_PATTERN = Pattern.compile("[（(]([A-Za-z0-9\\p{IsHan}\\-\\s]{2,40})[）)]");
    private static final Pattern DATE_RANGE_PATTERN = Pattern.compile("(20\\d{2})[年\\-./](\\d{1,2})(?:[月\\-./](\\d{1,2}))?日?\\s*(?:至|到|~|—|-|–)\\s*(20\\d{2})[年\\-./](\\d{1,2})(?:[月\\-./](\\d{1,2}))?日?");
    private static final Pattern SINGLE_DATE_PATTERN = Pattern.compile("(20\\d{2})[年\\-./](\\d{1,2})(?:[月\\-./](\\d{1,2}))?日?");

    private final ArchiveRecordMapper archiveRecordMapper;
    private final ArchiveExtValueMapper archiveExtValueMapper;
    private final ArchiveCreateSessionMapper archiveCreateSessionMapper;
    private final ArchiveAttachmentMapper archiveAttachmentMapper;
    private final ArchivePaperMapper archivePaperMapper;
    private final ArchiveContentMapper archiveContentMapper;
    private final ArchiveContentChunkMapper archiveContentChunkMapper;
    private final BindBatchMapper bindBatchMapper;
    private final BindVolumeMapper bindVolumeMapper;
    private final BindVolumeItemMapper bindVolumeItemMapper;
    private final StorageBatchMapper storageBatchMapper;
    private final StorageBatchItemMapper storageBatchItemMapper;
    private final StorageLedgerMapper storageLedgerMapper;
    private final AiModelConfigMapper aiModelConfigMapper;
    private final DocumentTypeMapper documentTypeMapper;
    private final CompanyProjectMapper companyProjectMapper;
    private final DocumentOrganizationMapper documentOrganizationMapper;
    private final DocumentOrganizationCityMapper documentOrganizationCityMapper;
    private final ArchiveFlowRuleMapper archiveFlowRuleMapper;
    private final SecurityLevelDictionaryMapper securityLevelDictionaryMapper;
    private final DocumentTypeExtFieldService documentTypeExtFieldService;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseLocationMapper warehouseLocationMapper;
    private final OperationAuditService operationAuditService;
    private final JdbcTemplate jdbcTemplate;
    private final ArchiveAiChatService archiveAiChatService;
    private final ArchiveFileTextExtractor archiveFileTextExtractor;
    private final ArchiveTextChunkService archiveTextChunkService;
    private final ArchiveTextVectorService archiveTextVectorService;
    private final ArchiveReceiptMapper archiveReceiptMapper;
    private final WorkflowService workflowService;

    @Override
    public ArchiveCreateOptionsResponse loadCreateOptions() {
        return ArchiveCreateOptionsResponse.builder()
            .companyProjects(listEnabledCompanyProjects())
            .documentTypes(listEnabledDocumentTypes())
            .archiveDestinations(listEnabledCities())
            .documentOrganizations(listEnabledDocumentOrganizations())
            .securityLevels(listSecurityLevels())
            .carrierTypes(loadDictionaryOptions("ARCHIVE_CARRIER_TYPE"))
            .attachmentTypes(loadDictionaryOptions("ARCHIVE_ATTACHMENT_TYPE"))
            .archiveTypes(loadDictionaryOptions("ARCHIVE_TYPE"))
            .aiModels(listAiModelOptions())
            .build();
    }

    @Override
    public ArchiveDefaultResolveResponse resolveDefaults(String companyProjectCode, String documentTypeCode, String customRule, String archiveDestination) {
        CompanyProject companyProject = requireCompanyProject(companyProjectCode);
        requireDocumentType(documentTypeCode);
        List<ArchiveFlowRule> rules = archiveFlowRuleMapper.selectList(new LambdaQueryWrapper<ArchiveFlowRule>()
            .eq(ArchiveFlowRule::getCompanyProjectCode, companyProjectCode)
            .eq(ArchiveFlowRule::getDocumentTypeCode, documentTypeCode)
            .eq(ArchiveFlowRule::getDeleteFlag, "N")
            .eq(ArchiveFlowRule::getEnabledFlag, "Y"));
        ArchiveFlowRule bestMatch = rules.stream().max(Comparator.comparingInt(rule -> scoreRule(rule, customRule, archiveDestination))).orElse(null);
        ArchiveDefaultResolveResponse response = new ArchiveDefaultResolveResponse();
        response.setCountryCode(companyProject.getCountryCode());
        if (bestMatch != null) {
            response.setSecurityLevelCode(bestMatch.getSecurityLevelCode());
            response.setArchiveDestination(StringUtils.hasText(archiveDestination) ? archiveDestination : bestMatch.getArchiveDestination());
            response.setDocumentOrganizationCode(bestMatch.getDocumentOrganizationCode());
            response.setRetentionPeriodYears(bestMatch.getRetentionPeriodYears());
        } else {
            response.setArchiveDestination(archiveDestination);
        }
        return response;
    }

    @Override
    @Transactional
    public ArchiveCreateSessionResponse createSession(ArchiveCreateSessionCommand command) {
        ArchiveCreateSession session = new ArchiveCreateSession();
        session.setSessionCode(generateCode("SES"));
        session.setCreateMode(normalizeCreateMode(command.getCreateMode()));
        session.setSessionStatus("READY");
        session.setParseStatus("PENDING");
        session.setOwnerUserId(SYSTEM_OPERATOR_ID);
        session.setExpireTime(LocalDateTime.now().plusDays(2));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        archiveCreateSessionMapper.insert(session);
        return buildSessionResponse(session, List.of(), null);
    }

    @Override
    public ArchiveCreateSessionResponse getSession(String sessionCode) {
        ArchiveCreateSession session = requireSession(sessionCode);
        List<ArchiveAttachment> attachments = listSessionAttachments(session.getSessionId());
        ArchiveAiParseResult parseResult = attachments.isEmpty() ? null : buildParseResult(session, attachments.get(attachments.size() - 1));
        return buildSessionResponse(session, attachments, parseResult);
    }

    @Override
    @Transactional
    public ArchiveAttachmentResponse uploadAttachment(String sessionCode, String attachmentRole, String attachmentTypeCode, String remark, MultipartFile file) {
        ArchiveCreateSession session = requireSession(sessionCode);
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Please upload a file");
        }
        Path storageRoot = Paths.get(System.getProperty("user.dir"), "storage", "archive-files", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        try { Files.createDirectories(storageRoot); } catch (IOException exception) { throw new BusinessException("Failed to create upload directory"); }
        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "upload.bin");
        String extension = extractExtension(originalFilename);
        String storageKey = UUID.randomUUID().toString().replace("-", "") + (StringUtils.hasText(extension) ? "." + extension : "");
        Path storagePath = storageRoot.resolve(storageKey);
        try { Files.copy(file.getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING); } catch (IOException exception) { throw new BusinessException("Failed to store uploaded file"); }
        ParsedAttachment parsed = parseStoredFile(storagePath, originalFilename, file.getContentType());
        ArchiveAttachment attachment = new ArchiveAttachment();
        attachment.setSessionId(session.getSessionId());
        attachment.setAttachmentRole(normalizeAttachmentRole(attachmentRole));
        String inferredAttachmentType = inferAttachmentTypeCode(originalFilename, parsed.summary(), parsed.preview());
        String inferredRemark = inferAttachmentRemark(originalFilename, parsed.summary(), parsed.preview());
        attachment.setAttachmentTypeCode(trimToNull(StringUtils.hasText(attachmentTypeCode) ? attachmentTypeCode : inferredAttachmentType));
        attachment.setAttachmentSeq(nextAttachmentSeq(session.getSessionId()));
        attachment.setVersionNo(1);
        attachment.setFileName(originalFilename);
        attachment.setFileExt(extension);
        attachment.setMimeType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setStoragePath(storagePath.toString());
        attachment.setStorageKey(storageKey);
        attachment.setFileHash(md5Hex(storagePath));
        attachment.setRemark(trimToNull(StringUtils.hasText(remark) ? remark : inferredRemark));
        attachment.setAiSummary(parsed.summary());
        attachment.setOcrStatus(parsed.hasText() ? "SUCCESS" : "FAILED");
        attachment.setParseStatus("SUCCESS");
        attachment.setVectorStatus(parsed.hasText() ? "PENDING" : "FAILED");
        attachment.setActiveFlag("Y");
        attachment.setDeleteFlag("N");
        attachment.setCreatedBy(SYSTEM_OPERATOR_ID);
        attachment.setCreationDate(LocalDateTime.now());
        attachment.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        attachment.setLastUpdateDate(LocalDateTime.now());
        archiveAttachmentMapper.insert(attachment);
        session.setDocumentTypeCodeGuess(trimToNull(parsed.suggestedDocumentTypeCode()));
        session.setCarrierTypeCodeGuess("ELECTRONIC");
        session.setParseStatus("SUCCESS");
        session.setAiSummarySnapshot(parsed.summary());
        session.setUpdatedAt(LocalDateTime.now());
        archiveCreateSessionMapper.updateById(session);
        return toAttachmentResponse(attachment);
    }

    @Override
    @Transactional
    public ArchiveAttachmentResponse updateAttachment(String sessionCode, Long attachmentId, ArchiveAttachmentUpdateCommand command) {
        ArchiveCreateSession session = requireSession(sessionCode);
        ArchiveAttachment attachment = archiveAttachmentMapper.selectOne(new LambdaQueryWrapper<ArchiveAttachment>()
            .eq(ArchiveAttachment::getAttachmentId, attachmentId)
            .eq(ArchiveAttachment::getSessionId, session.getSessionId())
            .eq(ArchiveAttachment::getDeleteFlag, "N")
            .last("limit 1"));
        if (attachment == null) {
            throw new BusinessException("Attachment not found");
        }
        attachment.setAttachmentTypeCode(trimToNull(command.getAttachmentTypeCode()));
        attachment.setRemark(trimToNull(command.getRemark()));
        attachment.setAiSummary(trimToNull(command.getAiSummary()));
        attachment.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        attachment.setLastUpdateDate(LocalDateTime.now());
        archiveAttachmentMapper.updateById(attachment);
        return toAttachmentResponse(attachment);
    }

    @Override
    @Transactional
    public ArchiveSummaryResponse createArchive(ArchiveCreateCommand command) {
        validateRequired(command);
        String documentTypeCode = requireText(command.getDocumentTypeCode(), "documentTypeCode");
        CompanyProject companyProject = requireCompanyProject(command.getCompanyProjectCode());
        requireDocumentType(documentTypeCode);
        ArchiveCreateSession session = StringUtils.hasText(command.getSessionCode()) ? requireSession(command.getSessionCode()) : null;
        List<ArchiveAttachment> sessionAttachments = session == null ? List.of() : listSessionAttachments(session.getSessionId());
        List<ArchiveAttachment> electronicAttachments = sessionAttachments.stream().filter(item -> "ELECTRONIC".equals(item.getAttachmentRole())).toList();
        List<ArchiveAttachment> paperScanAttachments = sessionAttachments.stream().filter(item -> "PAPER_SCAN".equals(item.getAttachmentRole())).toList();
        if (requiresElectronic(command.getCarrierTypeCode()) && electronicAttachments.isEmpty()) {
            throw new BusinessException("At least one electronic attachment is required");
        }
        if (requiresPaper(command.getCarrierTypeCode()) && (command.getPaperInfo() == null || command.getPaperInfo().getPlannedCopyCount() == null || command.getPaperInfo().getActualCopyCount() == null)) {
            throw new BusinessException("Paper archive information is required");
        }
        List<DocumentTypeExtFieldResponse> effectiveFields = documentTypeExtFieldService.listEffective(documentTypeCode);
        ParsedAttachment combinedParse = buildCombinedParseResult(electronicAttachments, documentTypeCode);
        Map<String, String> resolvedExtValues = resolveExtValues(command.getExtValues(), effectiveFields, combinedParse);
        validateExtValues(effectiveFields, resolvedExtValues);
        ArchiveRecord archive = new ArchiveRecord();
        archive.setArchiveCode(generateCode("ARC"));
        archive.setArchiveFilingCode(generateCode("FILE"));
        archive.setCreateMode(StringUtils.hasText(command.getCreateMode()) ? normalizeCreateMode(command.getCreateMode()) : (session == null ? "MANUAL" : session.getCreateMode()));
        archive.setArchiveStatus("CREATED");
        archive.setDocumentTypeCode(documentTypeCode);
        archive.setCompanyProjectCode(command.getCompanyProjectCode().trim());
        archive.setBeginPeriod(command.getBeginPeriod().trim());
        archive.setEndPeriod(command.getEndPeriod().trim());
        archive.setBusinessCode(trimToNull(command.getBusinessCode()));
        archive.setDocumentName(command.getDocumentName().trim());
        archive.setDutyPerson(command.getDutyPerson().trim());
        archive.setDutyDepartment(command.getDutyDepartment().trim());
        archive.setDocumentDate(command.getDocumentDate());
        archive.setSecurityLevelCode(command.getSecurityLevelCode().trim());
        archive.setSourceSystem(trimToNull(command.getSourceSystem()));
        archive.setArchiveDestination(trimToNull(command.getArchiveDestination()));
        archive.setOriginPlace(trimToNull(command.getOriginPlace()));
        archive.setCarrierTypeCode(normalizeCarrierType(command.getCarrierTypeCode()));
        archive.setRemark(trimToNull(command.getRemark()));
        archive.setAiArchiveSummary(StringUtils.hasText(command.getAiArchiveSummary()) ? command.getAiArchiveSummary().trim() : generateArchiveSummary(command, electronicAttachments, paperScanAttachments));
        archive.setAiParseConfidence(combinedParse.confidence() == null ? null : BigDecimal.valueOf(combinedParse.confidence()));
        archive.setDocumentOrganizationCode(command.getDocumentOrganizationCode().trim());
        archive.setRetentionPeriodYears(command.getRetentionPeriodYears());
        archive.setArchiveTypeCode(command.getArchiveTypeCode().trim());
        archive.setCountryCode(StringUtils.hasText(command.getCountryCode()) ? command.getCountryCode().trim() : companyProject.getCountryCode());
        archive.setParseStatus(electronicAttachments.isEmpty() ? "FAILED" : "SUCCESS");
        archive.setVectorStatus(electronicAttachments.isEmpty() ? "FAILED" : "SUCCESS");
        archive.setQaIndexStatus(electronicAttachments.isEmpty() ? "FAILED" : "SUCCESS");
        archive.setSessionId(session == null ? null : session.getSessionId());
        archive.setDeleteFlag("N");
        archive.setCreatedBy(SYSTEM_OPERATOR_ID);
        archive.setCreationDate(LocalDateTime.now());
        archive.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        archive.setLastUpdateDate(LocalDateTime.now());
        archiveRecordMapper.insert(archive);
        persistExtValues(archive.getArchiveId(), effectiveFields, resolvedExtValues);
        if (requiresPaper(command.getCarrierTypeCode())) {
            ArchivePaper paper = new ArchivePaper();
            paper.setArchiveId(archive.getArchiveId());
            paper.setPlannedCopyCount(command.getPaperInfo().getPlannedCopyCount());
            paper.setActualCopyCount(command.getPaperInfo().getActualCopyCount());
            paper.setRemark(trimToNull(command.getPaperInfo().getRemark()));
            paper.setCreatedBy(SYSTEM_OPERATOR_ID);
            paper.setCreationDate(LocalDateTime.now());
            paper.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            paper.setLastUpdateDate(LocalDateTime.now());
            archivePaperMapper.insert(paper);
        }
        for (ArchiveAttachment attachment : mergeAttachments(electronicAttachments, paperScanAttachments)) {
            attachment.setArchiveId(archive.getArchiveId());
            attachment.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            attachment.setLastUpdateDate(LocalDateTime.now());
            archiveAttachmentMapper.updateById(attachment);
        }
        for (ArchiveAttachment attachment : electronicAttachments) {
            persistContentAndVectors(archive.getArchiveId(), attachment, parseStoredFile(Paths.get(attachment.getStoragePath()), attachment.getFileName(), attachment.getMimeType()));
        }
        if (session != null) {
            session.setSessionStatus("SAVED");
            session.setUpdatedAt(LocalDateTime.now());
            archiveCreateSessionMapper.updateById(session);
        }
        return buildArchiveSummary(archive, loadExtValueMap(List.of(archive.getArchiveId())).getOrDefault(archive.getArchiveId(), Map.of()), mergeAttachments(electronicAttachments, paperScanAttachments));
    }

    @Override
    public ArchiveQueryResponse queryArchives(ArchiveQueryCommand command) {
        LambdaQueryWrapper<ArchiveRecord> wrapper = new LambdaQueryWrapper<ArchiveRecord>()
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .eq(StringUtils.hasText(command.getDocumentTypeCode()), ArchiveRecord::getDocumentTypeCode, trimToNull(command.getDocumentTypeCode()))
            .eq(StringUtils.hasText(command.getCompanyProjectCode()), ArchiveRecord::getCompanyProjectCode, trimToNull(command.getCompanyProjectCode()))
            .eq(StringUtils.hasText(command.getArchiveTypeCode()), ArchiveRecord::getArchiveTypeCode, trimToNull(command.getArchiveTypeCode()))
            .eq(StringUtils.hasText(command.getCarrierTypeCode()), ArchiveRecord::getCarrierTypeCode, trimToNull(command.getCarrierTypeCode()))
            .eq(StringUtils.hasText(command.getSecurityLevelCode()), ArchiveRecord::getSecurityLevelCode, trimToNull(command.getSecurityLevelCode()))
            .eq(StringUtils.hasText(command.getBeginPeriod()), ArchiveRecord::getBeginPeriod, trimToNull(command.getBeginPeriod()))
            .eq(StringUtils.hasText(command.getEndPeriod()), ArchiveRecord::getEndPeriod, trimToNull(command.getEndPeriod()))
            .like(StringUtils.hasText(command.getDocumentName()), ArchiveRecord::getDocumentName, trimToNull(command.getDocumentName()))
            .like(StringUtils.hasText(command.getBusinessCode()), ArchiveRecord::getBusinessCode, trimToNull(command.getBusinessCode()))
            .like(StringUtils.hasText(command.getDutyPerson()), ArchiveRecord::getDutyPerson, trimToNull(command.getDutyPerson()))
            .eq(StringUtils.hasText(command.getArchiveDestination()), ArchiveRecord::getArchiveDestination, trimToNull(command.getArchiveDestination()))
            .like(StringUtils.hasText(command.getSourceSystem()), ArchiveRecord::getSourceSystem, trimToNull(command.getSourceSystem()))
            .eq(StringUtils.hasText(command.getDocumentOrganizationCode()), ArchiveRecord::getDocumentOrganizationCode, trimToNull(command.getDocumentOrganizationCode()));

        List<ArchiveRecord> allRecords = archiveRecordMapper.selectList(wrapper);
        List<Long> archiveIds = allRecords.stream().map(ArchiveRecord::getArchiveId).toList();
        Map<Long, Map<String, String>> extValueMap = loadExtValueMap(archiveIds);
        Map<Long, List<ArchiveAttachment>> archiveAttachmentMap = loadAttachmentMap(archiveIds);

        if (StringUtils.hasText(command.getKeyword())) {
            List<String> searchTerms = buildNormalizedKeywordSearchTerms(command.getKeyword());
            Set<Long> keywordMatchedArchiveIds = loadUnifiedKeywordMatchedArchiveIds(searchTerms);
            Set<Long> inMemoryMatchedArchiveIds = collectInMemoryMatchedArchiveIds(allRecords, extValueMap, archiveAttachmentMap, searchTerms);
            Set<Long> matchedArchiveIds = new LinkedHashSet<>(keywordMatchedArchiveIds);
            matchedArchiveIds.addAll(inMemoryMatchedArchiveIds);
            log.info(
                "Archive query keyword='{}', searchTerms={}, sqlMatchedArchiveIdsCount={}, inMemoryMatchedArchiveIdsCount={}, containsArchive14={}",
                command.getKeyword(),
                searchTerms,
                keywordMatchedArchiveIds.size(),
                inMemoryMatchedArchiveIds.size(),
                matchedArchiveIds.contains(14L)
            );
            allRecords = allRecords.stream()
                .filter(item -> matchedArchiveIds.contains(item.getArchiveId()))
                .toList();
            archiveIds = allRecords.stream().map(ArchiveRecord::getArchiveId).toList();
            extValueMap = loadExtValueMap(archiveIds);
        }

        Map<String, String> extFilters = command.getExtFilters();
        if (extFilters != null && !extFilters.isEmpty()) {
            Map<Long, Map<String, String>> currentExtValueMap = extValueMap;
            allRecords = allRecords.stream().filter(item -> matchesExtFilters(currentExtValueMap.getOrDefault(item.getArchiveId(), Map.of()), extFilters)).toList();
            archiveIds = allRecords.stream().map(ArchiveRecord::getArchiveId).toList();
            extValueMap = loadExtValueMap(archiveIds);
        }

        long total = allRecords.size();
        Integer page = command.getPage() != null && command.getPage() > 0 ? command.getPage() : 1;
        Integer pageSize = command.getPageSize() != null && command.getPageSize() > 0 ? command.getPageSize() : 20;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());
        List<ArchiveRecord> paginatedRecords = start < end ? allRecords.subList(start, end) : List.of();

        List<Long> paginatedArchiveIds = paginatedRecords.stream().map(ArchiveRecord::getArchiveId).toList();
        Map<Long, List<ArchiveAttachment>> attachmentMap = loadAttachmentMap(paginatedArchiveIds);
        Map<Long, Map<String, String>> finalExtValueMap = extValueMap;
        Map<Long, List<ArchiveAttachment>> finalAttachmentMap = attachmentMap;
        List<DocumentTypeExtFieldResponse> queryFields = StringUtils.hasText(command.getDocumentTypeCode()) ? documentTypeExtFieldService.listEffective(command.getDocumentTypeCode()).stream().filter(item -> "Y".equals(item.getQueryEnabledFlag())).toList() : List.of();

        return ArchiveQueryResponse.builder()
            .records(paginatedRecords.stream().map(item -> buildArchiveSummary(item, finalExtValueMap.getOrDefault(item.getArchiveId(), Map.of()), finalAttachmentMap.getOrDefault(item.getArchiveId(), List.of()))).toList())
            .queryFields(queryFields)
            .total(total)
            .page(page)
            .pageSize(pageSize)
            .build();
    }

    @Override
    public ArchiveAskResponse ask(ArchiveAskCommand command) {
        AiModelConfig chatModel = findEnabledChatModel();
        List<ArchiveSummaryResponse> references = searchAskReferences(command, chatModel);
        if (references.isEmpty()) {
            return ArchiveAskResponse.builder()
                .answer("当前未检索到与你的问题直接相关的档案内容，建议换一种问法、缩小问题范围，或直接查看文档搜索结果。")
                .references(List.of())
                .build();
        }

        List<String> evidenceSnippets = references.stream()
            .map(this::buildAskEvidenceSnippet)
            .filter(StringUtils::hasText)
            .limit(6)
            .toList();

        String answer = archiveAiChatService.answer(chatModel, command.getQuestion(), references, evidenceSnippets);
        return ArchiveAskResponse.builder().answer(answer).references(references).build();
    }

    @Override
    public List<AiModelConfigResponse> listAiModels() {
        return aiModelConfigMapper.selectList(new LambdaQueryWrapper<AiModelConfig>().eq(AiModelConfig::getDeleteFlag, "N").eq(AiModelConfig::getEnabledFlag, "Y").orderByAsc(AiModelConfig::getModelType).orderByAsc(AiModelConfig::getModelCode)).stream().map(item -> AiModelConfigResponse.builder().modelCode(item.getModelCode()).modelName(item.getModelName()).modelType(item.getModelType()).modelIdentifier(item.getModelIdentifier()).embeddingDimension(item.getEmbeddingDimension()).timeoutSeconds(item.getTimeoutSeconds()).topK(item.getTopK()).scoreThreshold(item.getScoreThreshold()).enabledFlag(item.getEnabledFlag()).remark(item.getRemark()).build()).toList();
    }

    @Override
    public ArchiveSummaryResponse getArchiveDetail(Long archiveId) {
        ArchiveRecord archive = archiveRecordMapper.selectOne(new LambdaQueryWrapper<ArchiveRecord>()
            .eq(ArchiveRecord::getArchiveId, archiveId)
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .last("limit 1"));
        if (archive == null) {
            throw new BusinessException("Archive not found");
        }
        Map<Long, Map<String, String>> extValueMap = loadExtValueMap(List.of(archiveId));
        Map<Long, List<ArchiveAttachment>> attachmentMap = loadAttachmentMap(List.of(archiveId));
        return buildArchiveSummary(archive, extValueMap.getOrDefault(archiveId, Map.of()), attachmentMap.getOrDefault(archiveId, List.of()));
    }

    @Override
    @Transactional
    public ArchiveTransferResponse transferArchives(ArchiveTransferCommand command) {
        if (command.getArchiveIds() == null || command.getArchiveIds().isEmpty()) {
            throw new BusinessException("Please select archives to transfer");
        }

        List<ArchiveRecord> archives = archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
            .in(ArchiveRecord::getArchiveId, command.getArchiveIds())
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .orderByAsc(ArchiveRecord::getArchiveId));
        if (archives.size() != command.getArchiveIds().size()) {
            throw new BusinessException("Some selected archives do not exist");
        }

        Map<Long, Map<String, String>> extValueMap = loadExtValueMap(command.getArchiveIds());
        Map<String, String> typeNameMap = listEnabledDocumentTypes().stream()
            .collect(Collectors.toMap(LabelValueOption::getCode, LabelValueOption::getName, (left, right) -> left, LinkedHashMap::new));

        String initiatorId = StringUtils.hasText(command.getInitiatorId()) ? command.getInitiatorId().trim() : String.valueOf(SYSTEM_OPERATOR_ID);
        String initiatorName = StringUtils.hasText(command.getInitiatorName()) ? command.getInitiatorName().trim() : "张三";
        String assigneeId = command.getAssigneeId().trim();
        String assigneeName = StringUtils.hasText(command.getAssigneeName()) ? command.getAssigneeName().trim() : assigneeId;
        LocalDateTime now = LocalDateTime.now();
        String businessKey = generateCode("TRF");

        List<Map<String, Object>> transferDocuments = archives.stream()
            .map(archive -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("archiveId", archive.getArchiveId());
                item.put("archiveCode", archive.getArchiveCode());
                item.put("archiveFilingCode", archive.getArchiveFilingCode());
                item.put("documentName", archive.getDocumentName());
                item.put("documentTypeCode", archive.getDocumentTypeCode());
                item.put("documentTypeName", typeNameMap.getOrDefault(archive.getDocumentTypeCode(), archive.getDocumentTypeCode()));
                item.put("businessCode", archive.getBusinessCode());
                item.put("documentOrganizationCode", archive.getDocumentOrganizationCode());
                item.put("extFields", extValueMap.getOrDefault(archive.getArchiveId(), Map.of()));
                return item;
            })
            .toList();

        Map<String, Object> transferForm = new LinkedHashMap<>();
        transferForm.put("assigneeId", assigneeId);
        transferForm.put("assigneeName", assigneeName);
        transferForm.put("transferMethod", trimToNull(command.getTransferMethod()));
        transferForm.put("logisticsCompany", trimToNull(command.getLogisticsCompany()));
        transferForm.put("trackingNumber", trimToNull(command.getTrackingNumber()));
        transferForm.put("remark", trimToNull(command.getRemark()));

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("assigneeId", assigneeId);
        variables.put("assigneeName", assigneeName);
        variables.put("initiatorId", initiatorId);
        variables.put("initiatorName", initiatorName);
        variables.put("transferMode", "ARCHIVE_QUERY_BATCH");
        variables.put("transferForm", transferForm);
        variables.put("transferDocuments", transferDocuments);

        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessDefinitionKey("documentTransfer");
        startProcessCommand.setBusinessKey(businessKey);
        startProcessCommand.setBusinessType("TRANSFER");
        startProcessCommand.setInitiatorId(initiatorId);
        startProcessCommand.setInitiatorName(initiatorName);
        startProcessCommand.setVariables(variables);
        WorkflowInstance workflowInstance = workflowService.startProcess(startProcessCommand);

        for (ArchiveRecord archive : archives) {
            archive.setArchiveStatus("TRANSFERRED");
            archive.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            archive.setLastUpdateDate(now);
            archiveRecordMapper.updateById(archive);

            ArchiveReceipt receipt = new ArchiveReceipt();
            receipt.setReceiptCode("REC-" + archive.getArchiveFilingCode());
            receipt.setSourceDept(trimToNull(archive.getDutyDepartment()));
            receipt.setArchiveTitle(archive.getDocumentName());
            receipt.setArchiveType(typeNameMap.getOrDefault(archive.getDocumentTypeCode(), archive.getDocumentTypeCode()));
            receipt.setSecurityLevel(archive.getSecurityLevelCode());
            receipt.setReceiveStatus("PENDING_REVIEW");
            receipt.setWorkflowInstanceCode(workflowInstance.getProcessInstanceId());
            receipt.setSubmittedBy(initiatorName);
            receipt.setSubmittedAt(now);
            archiveReceiptMapper.insert(receipt);
        }

        operationAuditService.record(
            "ARCHIVE_TRANSFER",
            "档案移交",
            "WORKFLOW",
            businessKey,
            "CREATE",
            "批量移交档案并发起电子流",
            null,
            Map.of("archiveCount", archives.size(), "processInstanceId", workflowInstance.getProcessInstanceId()),
            SYSTEM_OPERATOR_ID,
            SYSTEM_OPERATOR_NAME
        );

        return ArchiveTransferResponse.builder()
            .businessKey(businessKey)
            .processInstanceId(workflowInstance.getProcessInstanceId())
            .workflowInstanceId(workflowInstance.getId())
            .archiveCount(archives.size())
            .archiveFilingCodes(archives.stream().map(ArchiveRecord::getArchiveFilingCode).toList())
            .build();
    }

    @Override
    public BindOptionsResponse loadBindOptions() {
        return BindOptionsResponse.builder()
            .bindModes(List.of(
                option("BUSINESS_CODE", "按文档类型+业务编号"),
                option("PERIOD", "按文档类型+期间"),
                option("MANUAL", "人工勾选")
            ))
            .candidates(listBindableArchiveCandidates(null, null, null))
            .build();
    }

    @Override
    public BindPreviewResponse previewBind(BindPreviewCommand command) {
        String bindMode = normalizeBindMode(command.getBindMode());
        List<ArchiveRecord> archives = loadBindCandidateArchives(command.getArchiveIds(), command.getKeyword(), command.getDocumentTypeCode(), command.getCompanyProjectCode());
        List<BindVolumeResponse> groups = buildPreviewGroups(bindMode, archives);
        return BindPreviewResponse.builder()
            .bindMode(bindMode)
            .groupCount(groups.size())
            .archiveCount(archives.size())
            .groups(groups)
            .build();
    }

    @Override
    @Transactional
    public BindBatchResponse createBindBatch(BindCreateCommand command) {
        String bindMode = normalizeBindMode(command.getBindMode());
        BindBatch bindBatch = new BindBatch();
        bindBatch.setBindBatchCode(generateCode("BND"));
        bindBatch.setBindMode(bindMode);
        bindBatch.setSourceType("ARCHIVE");
        bindBatch.setBindStatus("BOUND");
        bindBatch.setBindRemark(trimToNull(command.getBindRemark()));
        bindBatch.setGuidedStorageFlag("N");
        bindBatch.setVolumeCount(command.getVolumes().size());
        bindBatch.setArchiveCount(command.getVolumes().stream().mapToInt(item -> item.getItems().size()).sum());
        bindBatch.setDeleteFlag("N");
        bindBatch.setCreatedBy(SYSTEM_OPERATOR_ID);
        bindBatch.setCreationDate(LocalDateTime.now());
        bindBatch.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        bindBatch.setLastUpdateDate(LocalDateTime.now());
        bindBatchMapper.insert(bindBatch);

        List<BindVolumeResponse> volumeResponses = new ArrayList<>();
        for (int index = 0; index < command.getVolumes().size(); index++) {
            BindCreateCommand.BindCreateVolumeCommand volumeCommand = command.getVolumes().get(index);
            List<Long> archiveIds = volumeCommand.getItems().stream().map(BindCreateCommand.BindCreateVolumeItemCommand::getArchiveId).toList();
            List<ArchiveRecord> archives = archiveIds.isEmpty() ? List.of() : archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
                .in(ArchiveRecord::getArchiveId, archiveIds)
                .eq(ArchiveRecord::getDeleteFlag, "N"));
            Map<Long, ArchiveRecord> archiveMap = archives.stream().collect(Collectors.toMap(ArchiveRecord::getArchiveId, Function.identity()));
            ensureArchivesCanBind(archiveIds, archiveMap);

            BindVolume volume = new BindVolume();
            volume.setBindBatchId(bindBatch.getBindBatchId());
            volume.setBindVolumeCode(generateCode("VOL"));
            volume.setVolumeTitle(resolveVolumeTitle(bindMode, volumeCommand, archives, index + 1));
            volume.setBindRuleKey(trimToNull(volumeCommand.getBindRuleKey()));
            volume.setCarrierTypeCode(resolveVolumeCarrierType(volumeCommand, archives));
            volume.setArchiveCount(archiveIds.size());
            volume.setTotalPageCount(resolveVolumePageCount(archiveIds));
            volume.setTotalCopyCount(resolveVolumeCopyCount(archiveIds));
            volume.setBindStatus("BOUND");
            volume.setRemark(trimToNull(volumeCommand.getRemark()));
            volume.setDeleteFlag("N");
            volume.setCreatedBy(SYSTEM_OPERATOR_ID);
            volume.setCreationDate(LocalDateTime.now());
            volume.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            volume.setLastUpdateDate(LocalDateTime.now());
            bindVolumeMapper.insert(volume);

            List<BindVolumeItemResponse> itemResponses = new ArrayList<>();
            for (int itemIndex = 0; itemIndex < volumeCommand.getItems().size(); itemIndex++) {
                BindCreateCommand.BindCreateVolumeItemCommand itemCommand = volumeCommand.getItems().get(itemIndex);
                ArchiveRecord archive = archiveMap.get(itemCommand.getArchiveId());
                if (archive == null) {
                    throw new BusinessException("Archive not found: " + itemCommand.getArchiveId());
                }
                BindVolumeItem item = new BindVolumeItem();
                item.setVolumeId(volume.getVolumeId());
                item.setArchiveId(archive.getArchiveId());
                item.setSortNo(itemCommand.getSortNo() == null ? itemIndex + 1 : itemCommand.getSortNo());
                item.setPrimaryFlag("Y".equalsIgnoreCase(itemCommand.getPrimaryFlag()) ? "Y" : "N");
                item.setBindReason(trimToNull(itemCommand.getBindReason()));
                item.setCreatedBy(SYSTEM_OPERATOR_ID);
                item.setCreationDate(LocalDateTime.now());
                item.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
                item.setLastUpdateDate(LocalDateTime.now());
                bindVolumeItemMapper.insert(item);

                archive.setArchiveStatus("BOUND");
                archive.setBindVolumeCode(volume.getBindVolumeCode());
                archive.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
                archive.setLastUpdateDate(LocalDateTime.now());
                archiveRecordMapper.updateById(archive);

                itemResponses.add(toBindVolumeItemResponse(item, archive));
            }
            volumeResponses.add(toBindVolumeResponse(volume, itemResponses));
        }
        operationAuditService.record("ARCHIVE_BIND", "档案成册", "BIND_BATCH", bindBatch.getBindBatchCode(), "CREATE", "创建成册批次", null, Map.of("archiveCount", bindBatch.getArchiveCount()), SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
        return buildBindBatchResponse(bindBatch, volumeResponses);
    }

    @Override
    public BindBatchResponse getBindBatch(String bindBatchCode) {
        BindBatch batch = requireBindBatch(bindBatchCode);
        return buildBindBatchResponse(batch, loadVolumesByBatch(batch.getBindBatchId()));
    }

    @Override
    public List<BindBatchResponse> queryBindBatches(BindQueryCommand command) {
        List<BindBatch> batches = bindBatchMapper.selectList(new LambdaQueryWrapper<BindBatch>()
            .eq(BindBatch::getDeleteFlag, "N")
            .eq(StringUtils.hasText(command.getBindMode()), BindBatch::getBindMode, trimToNull(command.getBindMode()))
            .eq(StringUtils.hasText(command.getBindStatus()), BindBatch::getBindStatus, trimToNull(command.getBindStatus()))
            .like(StringUtils.hasText(command.getKeyword()), BindBatch::getBindBatchCode, trimToNull(command.getKeyword()))
            .orderByDesc(BindBatch::getCreationDate)
            .last("limit 50"));
        return batches.stream().map(item -> buildBindBatchResponse(item, loadVolumesByBatch(item.getBindBatchId()))).toList();
    }

    @Override
    public StorageOptionsResponse loadStorageOptions() {
        List<LabelValueOption> warehouseOptions = warehouseMapper.selectList(new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getStatus, "ACTIVE")
                .orderByAsc(Warehouse::getWarehouseCode))
            .stream()
            .map(item -> option(item.getWarehouseCode(), item.getWarehouseName()))
            .toList();
        List<LabelValueOption> locationOptions = warehouseLocationMapper.selectList(new LambdaQueryWrapper<WarehouseLocation>()
                .eq(WarehouseLocation::getDeleted, 0)
                .orderByAsc(WarehouseLocation::getWarehouseCode)
                .orderByAsc(WarehouseLocation::getLocationCode))
            .stream()
            .map(item -> option(item.getLocationCode(), item.getLocationName()))
            .toList();
        return StorageOptionsResponse.builder()
            .sourceTypes(List.of(option("BIND_GUIDED", "成册引导"), option("DIRECT", "独立入库")))
            .warehouses(warehouseOptions)
            .locations(locationOptions)
            .build();
    }

    @Override
    public StorageQueryResponse queryStorage(StorageQueryCommand command) {
        List<BindVolumeResponse> volumes = loadStorageCandidateVolumes(command.getSourceBindBatchCode(), command.getKeyword());
        List<BindArchiveCandidateResponse> archives = loadStandaloneStorageArchives(command.getKeyword());
        return StorageQueryResponse.builder().volumes(volumes).archives(archives).build();
    }

    @Override
    @Transactional
    public StorageBatchResponse createStorageBatch(StorageCreateCommand command) {
        String sourceType = normalizeStorageSourceType(command.getSourceType());
        Warehouse warehouse = requireWarehouse(command.getWarehouseCode());
        StorageBatch batch = new StorageBatch();
        batch.setStorageBatchCode(generateCode("STO"));
        batch.setSourceType(sourceType);
        batch.setSourceBindBatchCode(trimToNull(command.getSourceBindBatchCode()));
        batch.setWarehouseCode(warehouse.getWarehouseCode());
        batch.setOperatorId(SYSTEM_OPERATOR_ID);
        batch.setOperatorName(SYSTEM_OPERATOR_NAME);
        batch.setStorageStatus("IN_PROGRESS");
        batch.setRemark(trimToNull(command.getRemark()));
        batch.setCreatedAt(LocalDateTime.now());
        batch.setUpdatedAt(LocalDateTime.now());
        storageBatchMapper.insert(batch);

        List<StorageBatchItemResponse> itemResponses = new ArrayList<>();
        boolean hasFailure = false;
        for (StorageCreateCommand.StorageCreateItemCommand itemCommand : command.getItems()) {
            StorageExecutionResult result = executeStorageItem(batch, warehouse, itemCommand);
            itemResponses.add(result.response());
            hasFailure = hasFailure || "FAILED".equals(result.response().getResultStatus());
        }
        batch.setStorageStatus(hasFailure ? "FAILED" : "COMPLETED");
        batch.setUpdatedAt(LocalDateTime.now());
        storageBatchMapper.updateById(batch);

        if (StringUtils.hasText(batch.getSourceBindBatchCode())) {
            BindBatch bindBatch = requireBindBatch(batch.getSourceBindBatchCode());
            bindBatch.setGuidedStorageFlag("Y");
            if (!hasUnstoredVolumes(bindBatch.getBindBatchId())) {
                bindBatch.setBindStatus("STORED");
            }
            bindBatch.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            bindBatch.setLastUpdateDate(LocalDateTime.now());
            bindBatchMapper.updateById(bindBatch);
        }
        operationAuditService.record("ARCHIVE_STORAGE", "档案入库", "STORAGE_BATCH", batch.getStorageBatchCode(), "CREATE", "创建入库批次", null, Map.of("warehouseCode", batch.getWarehouseCode(), "status", batch.getStorageStatus()), SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
        return StorageBatchResponse.builder()
            .storageBatchId(batch.getStorageBatchId())
            .storageBatchCode(batch.getStorageBatchCode())
            .sourceType(batch.getSourceType())
            .sourceBindBatchCode(batch.getSourceBindBatchCode())
            .warehouseCode(batch.getWarehouseCode())
            .operatorName(batch.getOperatorName())
            .storageStatus(batch.getStorageStatus())
            .remark(batch.getRemark())
            .createdAt(batch.getCreatedAt())
            .items(itemResponses)
            .build();
    }

    @Override
    public StorageBatchResponse getStorageBatch(String storageBatchCode) {
        StorageBatch batch = requireStorageBatch(storageBatchCode);
        List<StorageBatchItemResponse> items = storageBatchItemMapper.selectList(new LambdaQueryWrapper<StorageBatchItem>()
                .eq(StorageBatchItem::getStorageBatchId, batch.getStorageBatchId())
                .orderByAsc(StorageBatchItem::getStorageItemId))
            .stream()
            .map(this::toStorageBatchItemResponse)
            .toList();
        return StorageBatchResponse.builder()
            .storageBatchId(batch.getStorageBatchId())
            .storageBatchCode(batch.getStorageBatchCode())
            .sourceType(batch.getSourceType())
            .sourceBindBatchCode(batch.getSourceBindBatchCode())
            .warehouseCode(batch.getWarehouseCode())
            .operatorName(batch.getOperatorName())
            .storageStatus(batch.getStorageStatus())
            .remark(batch.getRemark())
            .createdAt(batch.getCreatedAt())
            .items(items)
            .build();
    }

    @Override
    public List<StorageLedgerResponse> queryStorageLedger(StorageLedgerQueryCommand command) {
        return storageLedgerMapper.selectList(new LambdaQueryWrapper<StorageLedger>()
                .eq(StringUtils.hasText(command.getStorageBatchCode()), StorageLedger::getStorageBatchCode, trimToNull(command.getStorageBatchCode()))
                .eq(StringUtils.hasText(command.getBindVolumeCode()), StorageLedger::getBindVolumeCode, trimToNull(command.getBindVolumeCode()))
                .eq(StringUtils.hasText(command.getArchiveCode()), StorageLedger::getArchiveCode, trimToNull(command.getArchiveCode()))
                .eq(StringUtils.hasText(command.getWarehouseCode()), StorageLedger::getWarehouseCode, trimToNull(command.getWarehouseCode()))
                .eq(StringUtils.hasText(command.getLocationCode()), StorageLedger::getLocationCode, trimToNull(command.getLocationCode()))
                .eq(StringUtils.hasText(command.getResultStatus()), StorageLedger::getResultStatus, trimToNull(command.getResultStatus()))
                .orderByDesc(StorageLedger::getOperationTime)
                .last("limit 200"))
            .stream()
            .map(this::toStorageLedgerResponse)
            .toList();
    }

    @Override
    public StorageLedgerResponse getStorageLedger(Long ledgerId) {
        StorageLedger ledger = storageLedgerMapper.selectById(ledgerId);
        if (ledger == null) {
            throw new BusinessException("Storage ledger not found");
        }
        return toStorageLedgerResponse(ledger);
    }

    private List<LabelValueOption> listEnabledCompanyProjects() {
        return companyProjectMapper.selectList(new LambdaQueryWrapper<CompanyProject>().eq(CompanyProject::getDeleteFlag, "N").eq(CompanyProject::getEnabledFlag, "Y").orderByAsc(CompanyProject::getCompanyProjectCode)).stream().map(item -> option(item.getCompanyProjectCode(), item.getCompanyProjectName())).toList();
    }

    private List<LabelValueOption> listEnabledDocumentTypes() {
        return documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>().eq(DocumentType::getDeleteFlag, "N").eq(DocumentType::getEnabledFlag, "Y").orderByAsc(DocumentType::getLevelNum).orderByAsc(DocumentType::getSortOrder).orderByAsc(DocumentType::getTypeCode)).stream().map(item -> option(item.getTypeCode(), item.getTypeName())).toList();
    }

    private List<LabelValueOption> listEnabledDocumentOrganizations() {
        return jdbcTemplate.query("select document_organization_code, document_organization_name from md_document_organization where enabled_flag = 'Y' and delete_flag = 'N' order by document_organization_code", (rs, rowNum) -> option(rs.getString(1), rs.getString(2)));
    }

    private List<LabelValueOption> listEnabledCities() {
        return documentOrganizationCityMapper.selectList(new LambdaQueryWrapper<DocumentOrganizationCity>().eq(DocumentOrganizationCity::getEnabledFlag, "Y").eq(DocumentOrganizationCity::getDeleteFlag, "N").orderByAsc(DocumentOrganizationCity::getCountryCode).orderByAsc(DocumentOrganizationCity::getSortOrder)).stream().map(item -> option(item.getCityCode(), item.getCityName())).toList();
    }

    private List<LabelValueOption> listSecurityLevels() {
        return securityLevelDictionaryMapper.selectList(new LambdaQueryWrapper<SecurityLevelDictionary>().eq(SecurityLevelDictionary::getDeleteFlag, "N").eq(SecurityLevelDictionary::getEnabledFlag, "Y").orderByAsc(SecurityLevelDictionary::getSortOrder)).stream().map(item -> option(item.getSecurityLevelCode(), item.getSecurityLevelName())).toList();
    }

    private List<LabelValueOption> listAiModelOptions() {
        return listAiModels().stream().map(item -> option(item.getModelCode(), item.getModelName())).toList();
    }

    private List<LabelValueOption> loadDictionaryOptions(String categoryCode) {
        return jdbcTemplate.query("select item_code, item_name from md_dict_item where category_code = ? and enabled_flag = 'Y' and delete_flag = 'N' order by sort_order, item_code", (rs, rowNum) -> option(rs.getString(1), rs.getString(2)), categoryCode);
    }

    private List<BindArchiveCandidateResponse> listBindableArchiveCandidates(String keyword, String documentTypeCode, String companyProjectCode) {
        return loadBindCandidateArchives(null, keyword, documentTypeCode, companyProjectCode).stream().map(this::toBindArchiveCandidateResponse).toList();
    }

    private List<ArchiveRecord> loadBindCandidateArchives(List<Long> archiveIds, String keyword, String documentTypeCode, String companyProjectCode) {
        LambdaQueryWrapper<ArchiveRecord> wrapper = new LambdaQueryWrapper<ArchiveRecord>()
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .ne(ArchiveRecord::getArchiveStatus, "STORED")
            .isNull(ArchiveRecord::getBindVolumeCode)
            .eq(StringUtils.hasText(documentTypeCode), ArchiveRecord::getDocumentTypeCode, trimToNull(documentTypeCode))
            .eq(StringUtils.hasText(companyProjectCode), ArchiveRecord::getCompanyProjectCode, trimToNull(companyProjectCode))
            .and(StringUtils.hasText(keyword), query -> query
                .like(ArchiveRecord::getDocumentName, trimToNull(keyword))
                .or()
                .like(ArchiveRecord::getArchiveCode, trimToNull(keyword))
                .or()
                .like(ArchiveRecord::getBusinessCode, trimToNull(keyword)))
            .orderByDesc(ArchiveRecord::getLastUpdateDate);
        if (archiveIds != null && !archiveIds.isEmpty()) {
            wrapper.in(ArchiveRecord::getArchiveId, archiveIds);
        } else {
            wrapper.last("limit 200");
        }
        return archiveRecordMapper.selectList(wrapper);
    }

    private List<BindVolumeResponse> buildPreviewGroups(String bindMode, List<ArchiveRecord> archives) {
        if (archives.isEmpty()) {
            return List.of();
        }
        List<ArchiveRecord> sortedArchives = archives.stream()
            .sorted(Comparator.comparing(ArchiveRecord::getDocumentTypeCode, Comparator.nullsLast(String::compareTo))
                .thenComparing(ArchiveRecord::getBusinessCode, Comparator.nullsLast(String::compareTo))
                .thenComparing(ArchiveRecord::getBeginPeriod, Comparator.nullsLast(String::compareTo))
                .thenComparing(ArchiveRecord::getArchiveCode))
            .toList();
        Map<String, List<ArchiveRecord>> grouped;
        if ("MANUAL".equals(bindMode)) {
            grouped = Map.of("MANUAL", sortedArchives);
        } else if ("BUSINESS_CODE".equals(bindMode)) {
            grouped = sortedArchives.stream().collect(Collectors.groupingBy(
                archive -> archive.getDocumentTypeCode() + "|" + Objects.toString(trimToNull(archive.getBusinessCode()), "NO_BUSINESS_CODE"),
                LinkedHashMap::new,
                Collectors.toList()
            ));
        } else {
            grouped = sortedArchives.stream().collect(Collectors.groupingBy(
                archive -> archive.getDocumentTypeCode() + "|" + archive.getBeginPeriod() + "~" + archive.getEndPeriod(),
                LinkedHashMap::new,
                Collectors.toList()
            ));
        }
        List<BindVolumeResponse> responses = new ArrayList<>();
        int groupIndex = 1;
        for (Map.Entry<String, List<ArchiveRecord>> entry : grouped.entrySet()) {
            List<BindVolumeItemResponse> items = new ArrayList<>();
            for (int index = 0; index < entry.getValue().size(); index++) {
                ArchiveRecord archive = entry.getValue().get(index);
                items.add(BindVolumeItemResponse.builder()
                    .archiveId(archive.getArchiveId())
                    .archiveCode(archive.getArchiveCode())
                    .documentName(archive.getDocumentName())
                    .sortNo(index + 1)
                    .primaryFlag(index == 0 ? "Y" : "N")
                    .bindReason(entry.getKey())
                    .build());
            }
            responses.add(BindVolumeResponse.builder()
                .volumeTitle(buildPreviewVolumeTitle(bindMode, entry.getValue(), groupIndex++))
                .bindRuleKey(entry.getKey())
                .carrierTypeCode(resolveVolumeCarrierType(null, entry.getValue()))
                .archiveCount(entry.getValue().size())
                .totalPageCount(resolveVolumePageCount(entry.getValue().stream().map(ArchiveRecord::getArchiveId).toList()))
                .totalCopyCount(resolveVolumeCopyCount(entry.getValue().stream().map(ArchiveRecord::getArchiveId).toList()))
                .bindStatus("DRAFT")
                .items(items)
                .build());
        }
        return responses;
    }

    private void ensureArchivesCanBind(List<Long> archiveIds, Map<Long, ArchiveRecord> archiveMap) {
        for (Long archiveId : archiveIds) {
            ArchiveRecord archive = archiveMap.get(archiveId);
            if (archive == null) {
                throw new BusinessException("Archive not found: " + archiveId);
            }
            if ("STORED".equals(archive.getArchiveStatus())) {
                throw new BusinessException("Stored archive cannot be bound again");
            }
            if (StringUtils.hasText(archive.getBindVolumeCode())) {
                throw new BusinessException("Archive is already bound: " + archive.getArchiveCode());
            }
        }
    }

    private String resolveVolumeTitle(String bindMode, BindCreateCommand.BindCreateVolumeCommand command, List<ArchiveRecord> archives, int index) {
        if (StringUtils.hasText(command.getVolumeTitle())) {
            return command.getVolumeTitle().trim();
        }
        return buildPreviewVolumeTitle(bindMode, archives, index);
    }

    private String buildPreviewVolumeTitle(String bindMode, List<ArchiveRecord> archives, int index) {
        ArchiveRecord sample = archives.get(0);
        if ("BUSINESS_CODE".equals(bindMode)) {
            return sample.getDocumentName() + "-" + Objects.toString(trimToNull(sample.getBusinessCode()), "未编码") + "-册" + index;
        }
        if ("PERIOD".equals(bindMode)) {
            return sample.getDocumentName() + "-" + sample.getBeginPeriod() + "~" + sample.getEndPeriod() + "-册" + index;
        }
        return sample.getDocumentName() + "-手工成册-" + index;
    }

    private String resolveVolumeCarrierType(BindCreateCommand.BindCreateVolumeCommand command, List<ArchiveRecord> archives) {
        if (command != null && StringUtils.hasText(command.getCarrierTypeCode())) {
            return normalizeCarrierType(command.getCarrierTypeCode());
        }
        Set<String> carrierTypes = archives.stream().map(ArchiveRecord::getCarrierTypeCode).filter(StringUtils::hasText).collect(Collectors.toSet());
        if (carrierTypes.size() == 1) {
            return carrierTypes.iterator().next();
        }
        return "HYBRID";
    }

    private Integer resolveVolumePageCount(List<Long> archiveIds) {
        if (archiveIds.isEmpty()) {
            return 0;
        }
        return archivePaperMapper.selectList(new LambdaQueryWrapper<ArchivePaper>().in(ArchivePaper::getArchiveId, archiveIds))
            .stream()
            .map(ArchivePaper::getActualCopyCount)
            .filter(Objects::nonNull)
            .reduce(0, Integer::sum);
    }

    private Integer resolveVolumeCopyCount(List<Long> archiveIds) {
        if (archiveIds.isEmpty()) {
            return 0;
        }
        return archivePaperMapper.selectList(new LambdaQueryWrapper<ArchivePaper>().in(ArchivePaper::getArchiveId, archiveIds))
            .stream()
            .map(ArchivePaper::getPlannedCopyCount)
            .filter(Objects::nonNull)
            .reduce(0, Integer::sum);
    }

    private BindArchiveCandidateResponse toBindArchiveCandidateResponse(ArchiveRecord archive) {
        return BindArchiveCandidateResponse.builder()
            .archiveId(archive.getArchiveId())
            .archiveCode(archive.getArchiveCode())
            .documentName(archive.getDocumentName())
            .documentTypeCode(archive.getDocumentTypeCode())
            .companyProjectCode(archive.getCompanyProjectCode())
            .businessCode(archive.getBusinessCode())
            .beginPeriod(archive.getBeginPeriod())
            .endPeriod(archive.getEndPeriod())
            .archiveStatus(archive.getArchiveStatus())
            .carrierTypeCode(archive.getCarrierTypeCode())
            .bindVolumeCode(archive.getBindVolumeCode())
            .build();
    }

    private BindVolumeItemResponse toBindVolumeItemResponse(BindVolumeItem item, ArchiveRecord archive) {
        return BindVolumeItemResponse.builder()
            .itemId(item.getItemId())
            .archiveId(archive.getArchiveId())
            .archiveCode(archive.getArchiveCode())
            .documentName(archive.getDocumentName())
            .sortNo(item.getSortNo())
            .primaryFlag(item.getPrimaryFlag())
            .bindReason(item.getBindReason())
            .build();
    }

    private BindVolumeResponse toBindVolumeResponse(BindVolume volume, List<BindVolumeItemResponse> items) {
        return BindVolumeResponse.builder()
            .volumeId(volume.getVolumeId())
            .bindVolumeCode(volume.getBindVolumeCode())
            .volumeTitle(volume.getVolumeTitle())
            .bindRuleKey(volume.getBindRuleKey())
            .carrierTypeCode(volume.getCarrierTypeCode())
            .archiveCount(volume.getArchiveCount())
            .totalPageCount(volume.getTotalPageCount())
            .totalCopyCount(volume.getTotalCopyCount())
            .bindStatus(volume.getBindStatus())
            .remark(volume.getRemark())
            .items(items)
            .build();
    }

    private BindBatchResponse buildBindBatchResponse(BindBatch batch, List<BindVolumeResponse> volumes) {
        return BindBatchResponse.builder()
            .bindBatchId(batch.getBindBatchId())
            .bindBatchCode(batch.getBindBatchCode())
            .bindMode(batch.getBindMode())
            .bindStatus(batch.getBindStatus())
            .bindRemark(batch.getBindRemark())
            .guidedStorageFlag(batch.getGuidedStorageFlag())
            .volumeCount(batch.getVolumeCount())
            .archiveCount(batch.getArchiveCount())
            .nextAction("GO_STORAGE")
            .creationDate(batch.getCreationDate())
            .volumes(volumes)
            .build();
    }

    private List<BindVolumeResponse> loadVolumesByBatch(Long bindBatchId) {
        List<BindVolume> volumes = bindVolumeMapper.selectList(new LambdaQueryWrapper<BindVolume>()
            .eq(BindVolume::getBindBatchId, bindBatchId)
            .eq(BindVolume::getDeleteFlag, "N")
            .orderByAsc(BindVolume::getCreationDate));
        if (volumes.isEmpty()) {
            return List.of();
        }
        List<Long> volumeIds = volumes.stream().map(BindVolume::getVolumeId).toList();
        List<BindVolumeItem> items = bindVolumeItemMapper.selectList(new LambdaQueryWrapper<BindVolumeItem>()
            .in(BindVolumeItem::getVolumeId, volumeIds)
            .orderByAsc(BindVolumeItem::getSortNo));
        List<Long> archiveIds = items.stream().map(BindVolumeItem::getArchiveId).distinct().toList();
        Map<Long, ArchiveRecord> archiveMap = archiveIds.isEmpty() ? Map.of() : archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
                .in(ArchiveRecord::getArchiveId, archiveIds)
                .eq(ArchiveRecord::getDeleteFlag, "N"))
            .stream()
            .collect(Collectors.toMap(ArchiveRecord::getArchiveId, Function.identity()));
        Map<Long, List<BindVolumeItemResponse>> itemMap = new LinkedHashMap<>();
        for (BindVolumeItem item : items) {
            itemMap.computeIfAbsent(item.getVolumeId(), key -> new ArrayList<>()).add(toBindVolumeItemResponse(item, archiveMap.get(item.getArchiveId())));
        }
        return volumes.stream().map(item -> toBindVolumeResponse(item, itemMap.getOrDefault(item.getVolumeId(), List.of()))).toList();
    }

    private List<BindVolumeResponse> loadStorageCandidateVolumes(String sourceBindBatchCode, String keyword) {
        List<BindVolume> volumes = bindVolumeMapper.selectList(new LambdaQueryWrapper<BindVolume>()
            .eq(BindVolume::getDeleteFlag, "N")
            .ne(BindVolume::getBindStatus, "STORED")
            .orderByDesc(BindVolume::getCreationDate)
            .last("limit 100"));
        if (StringUtils.hasText(sourceBindBatchCode)) {
            BindBatch batch = requireBindBatch(sourceBindBatchCode);
            volumes = volumes.stream().filter(item -> Objects.equals(item.getBindBatchId(), batch.getBindBatchId())).toList();
        }
        if (StringUtils.hasText(keyword)) {
            String normalizedKeyword = trimToNull(keyword);
            volumes = volumes.stream()
                .filter(item -> containsIgnoreCase(item.getBindVolumeCode(), normalizedKeyword.toLowerCase(Locale.ROOT)) || containsIgnoreCase(item.getVolumeTitle(), normalizedKeyword.toLowerCase(Locale.ROOT)))
                .toList();
        }
        if (volumes.isEmpty()) {
            return List.of();
        }
        List<BindVolume> selectedVolumes = volumes;
        Map<Long, List<BindVolumeResponse>> groupedResponses = volumes.stream()
            .collect(Collectors.groupingBy(BindVolume::getBindBatchId, LinkedHashMap::new, Collectors.collectingAndThen(Collectors.toList(), group -> loadVolumesByBatch(group.get(0).getBindBatchId()))));
        return groupedResponses.values().stream().flatMap(List::stream).filter(item -> selectedVolumes.stream().anyMatch(volume -> Objects.equals(volume.getVolumeId(), item.getVolumeId()))).toList();
    }

    private List<BindArchiveCandidateResponse> loadStandaloneStorageArchives(String keyword) {
        return archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
                .eq(ArchiveRecord::getDeleteFlag, "N")
                .isNull(ArchiveRecord::getBindVolumeCode)
                .ne(ArchiveRecord::getArchiveStatus, "STORED")
                .and(StringUtils.hasText(keyword), query -> query.like(ArchiveRecord::getArchiveCode, trimToNull(keyword)).or().like(ArchiveRecord::getDocumentName, trimToNull(keyword)))
                .orderByDesc(ArchiveRecord::getLastUpdateDate)
                .last("limit 100"))
            .stream()
            .map(this::toBindArchiveCandidateResponse)
            .toList();
    }

    private StorageExecutionResult executeStorageItem(StorageBatch batch, Warehouse warehouse, StorageCreateCommand.StorageCreateItemCommand itemCommand) {
        String itemType = normalizeStorageItemType(itemCommand.getItemType());
        WarehouseLocation location = requireAvailableLocation(warehouse.getWarehouseCode(), itemCommand.getLocationCode());
        if ("VOLUME".equals(itemType)) {
            BindVolume volume = requireBindVolume(itemCommand.getVolumeId());
            List<BindVolumeItem> volumeItems = bindVolumeItemMapper.selectList(new LambdaQueryWrapper<BindVolumeItem>()
                .eq(BindVolumeItem::getVolumeId, volume.getVolumeId())
                .orderByAsc(BindVolumeItem::getSortNo));
            List<Long> archiveIds = volumeItems.stream().map(BindVolumeItem::getArchiveId).toList();
            List<ArchiveRecord> archives = archiveIds.isEmpty() ? List.of() : archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
                .in(ArchiveRecord::getArchiveId, archiveIds)
                .eq(ArchiveRecord::getDeleteFlag, "N"));
            ensureLocationCapacity(location, archives.size());

            StorageBatchItem batchItem = new StorageBatchItem();
            batchItem.setStorageBatchId(batch.getStorageBatchId());
            batchItem.setItemType(itemType);
            batchItem.setVolumeId(volume.getVolumeId());
            batchItem.setBindVolumeCode(volume.getBindVolumeCode());
            batchItem.setLocationCode(location.getLocationCode());
            batchItem.setResultStatus("SUCCESS");
            batchItem.setStoredAt(LocalDateTime.now());
            storageBatchItemMapper.insert(batchItem);

            for (ArchiveRecord archive : archives) {
                updateArchiveStorageState(archive, warehouse, location, volume.getBindVolumeCode());
                createStorageLedger(batch, itemType, batch.getSourceBindBatchCode(), volume.getBindVolumeCode(), archive.getArchiveCode(), warehouse.getWarehouseCode(), location.getLocationCode(), "SUCCESS", "册内档案入库成功");
            }
            volume.setBindStatus("STORED");
            volume.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            volume.setLastUpdateDate(LocalDateTime.now());
            bindVolumeMapper.updateById(volume);
            occupyLocation(location, archives.size());
            operationAuditService.record("ARCHIVE_STORAGE", "档案入库", "VOLUME", volume.getBindVolumeCode(), "STORE", "册入库", null, Map.of("locationCode", location.getLocationCode()), SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
            return new StorageExecutionResult(toStorageBatchItemResponse(batchItem));
        }

        ArchiveRecord archive = requireArchive(itemCommand.getArchiveId());
        ensureLocationCapacity(location, 1);
        StorageBatchItem batchItem = new StorageBatchItem();
        batchItem.setStorageBatchId(batch.getStorageBatchId());
        batchItem.setItemType(itemType);
        batchItem.setArchiveId(archive.getArchiveId());
        batchItem.setArchiveCode(archive.getArchiveCode());
        batchItem.setLocationCode(location.getLocationCode());
        batchItem.setResultStatus("SUCCESS");
        batchItem.setStoredAt(LocalDateTime.now());
        storageBatchItemMapper.insert(batchItem);
        updateArchiveStorageState(archive, warehouse, location, archive.getBindVolumeCode());
        occupyLocation(location, 1);
        createStorageLedger(batch, itemType, batch.getSourceBindBatchCode(), archive.getBindVolumeCode(), archive.getArchiveCode(), warehouse.getWarehouseCode(), location.getLocationCode(), "SUCCESS", "档案入库成功");
        operationAuditService.record("ARCHIVE_STORAGE", "档案入库", "ARCHIVE", archive.getArchiveCode(), "STORE", "档案入库", null, Map.of("locationCode", location.getLocationCode()), SYSTEM_OPERATOR_ID, SYSTEM_OPERATOR_NAME);
        return new StorageExecutionResult(toStorageBatchItemResponse(batchItem));
    }

    private void updateArchiveStorageState(ArchiveRecord archive, Warehouse warehouse, WarehouseLocation location, String bindVolumeCode) {
        archive.setArchiveStatus("STORED");
        archive.setBindVolumeCode(trimToNull(bindVolumeCode));
        archive.setCurrentWarehouseCode(warehouse.getWarehouseCode());
        archive.setCurrentLocationCode(location.getLocationCode());
        archive.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        archive.setLastUpdateDate(LocalDateTime.now());
        archiveRecordMapper.updateById(archive);
    }

    private void createStorageLedger(StorageBatch batch, String itemType, String bindBatchCode, String bindVolumeCode, String archiveCode, String warehouseCode, String locationCode, String resultStatus, String summary) {
        StorageLedger ledger = new StorageLedger();
        ledger.setLedgerCode(generateCode("LED"));
        ledger.setStorageBatchCode(batch.getStorageBatchCode());
        ledger.setItemType(itemType);
        ledger.setBindBatchCode(trimToNull(bindBatchCode));
        ledger.setBindVolumeCode(trimToNull(bindVolumeCode));
        ledger.setArchiveCode(trimToNull(archiveCode));
        ledger.setWarehouseCode(warehouseCode);
        ledger.setLocationCode(locationCode);
        ledger.setActionType("STORE");
        ledger.setResultStatus(resultStatus);
        ledger.setOperatorId(SYSTEM_OPERATOR_ID);
        ledger.setOperatorName(SYSTEM_OPERATOR_NAME);
        ledger.setOperationTime(LocalDateTime.now());
        ledger.setOperationSummary(summary);
        storageLedgerMapper.insert(ledger);
    }

    private void ensureLocationCapacity(WarehouseLocation location, int increment) {
        if (!List.of("FREE", "OCCUPIED", "WARNING").contains(location.getStatus())) {
            throw new BusinessException("Location is not available for storage");
        }
        int occupiedCount = Objects.requireNonNullElse(location.getOccupiedCount(), 0);
        int capacity = Objects.requireNonNullElse(location.getCapacity(), 0);
        if (capacity <= 0 || occupiedCount + increment > capacity) {
            throw new BusinessException("Location capacity exceeded");
        }
    }

    private void occupyLocation(WarehouseLocation location, int increment) {
        int occupiedCount = Objects.requireNonNullElse(location.getOccupiedCount(), 0) + increment;
        location.setOccupiedCount(occupiedCount);
        location.setStatus(occupiedCount >= Objects.requireNonNullElse(location.getCapacity(), 0) ? "WARNING" : "OCCUPIED");
        location.setUtilizationRate(calculateRate(occupiedCount, location.getCapacity()));
        location.setUpdatedAt(LocalDateTime.now());
        warehouseLocationMapper.updateById(location);
    }

    private BigDecimal calculateRate(Integer occupiedCount, Integer capacity) {
        if (capacity == null || capacity <= 0 || occupiedCount == null || occupiedCount <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(occupiedCount * 100.0 / capacity).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private StorageBatchItemResponse toStorageBatchItemResponse(StorageBatchItem item) {
        return StorageBatchItemResponse.builder()
            .storageItemId(item.getStorageItemId())
            .itemType(item.getItemType())
            .volumeId(item.getVolumeId())
            .archiveId(item.getArchiveId())
            .bindVolumeCode(item.getBindVolumeCode())
            .archiveCode(item.getArchiveCode())
            .locationCode(item.getLocationCode())
            .resultStatus(item.getResultStatus())
            .errorMessage(item.getErrorMessage())
            .storedAt(item.getStoredAt())
            .build();
    }

    private StorageLedgerResponse toStorageLedgerResponse(StorageLedger ledger) {
        return StorageLedgerResponse.builder()
            .ledgerId(ledger.getLedgerId())
            .ledgerCode(ledger.getLedgerCode())
            .storageBatchCode(ledger.getStorageBatchCode())
            .itemType(ledger.getItemType())
            .bindBatchCode(ledger.getBindBatchCode())
            .bindVolumeCode(ledger.getBindVolumeCode())
            .archiveCode(ledger.getArchiveCode())
            .warehouseCode(ledger.getWarehouseCode())
            .locationCode(ledger.getLocationCode())
            .actionType(ledger.getActionType())
            .resultStatus(ledger.getResultStatus())
            .operatorName(ledger.getOperatorName())
            .operationTime(ledger.getOperationTime())
            .operationSummary(ledger.getOperationSummary())
            .build();
    }

    private BindBatch requireBindBatch(String bindBatchCode) {
        BindBatch batch = bindBatchMapper.selectOne(new LambdaQueryWrapper<BindBatch>()
            .eq(BindBatch::getBindBatchCode, bindBatchCode)
            .eq(BindBatch::getDeleteFlag, "N")
            .last("limit 1"));
        if (batch == null) {
            throw new BusinessException("Bind batch not found");
        }
        return batch;
    }

    private BindVolume requireBindVolume(Long volumeId) {
        BindVolume volume = bindVolumeMapper.selectOne(new LambdaQueryWrapper<BindVolume>()
            .eq(BindVolume::getVolumeId, volumeId)
            .eq(BindVolume::getDeleteFlag, "N")
            .last("limit 1"));
        if (volume == null) {
            throw new BusinessException("Bind volume not found");
        }
        if ("STORED".equals(volume.getBindStatus())) {
            throw new BusinessException("Bind volume is already stored");
        }
        return volume;
    }

    private ArchiveRecord requireArchive(Long archiveId) {
        ArchiveRecord archive = archiveRecordMapper.selectOne(new LambdaQueryWrapper<ArchiveRecord>()
            .eq(ArchiveRecord::getArchiveId, archiveId)
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .last("limit 1"));
        if (archive == null) {
            throw new BusinessException("Archive not found");
        }
        if ("STORED".equals(archive.getArchiveStatus())) {
            throw new BusinessException("Archive is already stored");
        }
        return archive;
    }

    private Warehouse requireWarehouse(String warehouseCode) {
        Warehouse warehouse = warehouseMapper.selectOne(new LambdaQueryWrapper<Warehouse>()
            .eq(Warehouse::getWarehouseCode, warehouseCode)
            .last("limit 1"));
        if (warehouse == null) {
            throw new BusinessException("Warehouse not found");
        }
        return warehouse;
    }

    private WarehouseLocation requireAvailableLocation(String warehouseCode, String locationCode) {
        WarehouseLocation location = warehouseLocationMapper.selectOne(new LambdaQueryWrapper<WarehouseLocation>()
            .eq(WarehouseLocation::getWarehouseCode, warehouseCode)
            .eq(WarehouseLocation::getLocationCode, locationCode)
            .eq(WarehouseLocation::getDeleted, 0)
            .last("limit 1"));
        if (location == null) {
            throw new BusinessException("Location not found");
        }
        return location;
    }

    private StorageBatch requireStorageBatch(String storageBatchCode) {
        StorageBatch batch = storageBatchMapper.selectOne(new LambdaQueryWrapper<StorageBatch>()
            .eq(StorageBatch::getStorageBatchCode, storageBatchCode)
            .last("limit 1"));
        if (batch == null) {
            throw new BusinessException("Storage batch not found");
        }
        return batch;
    }

    private boolean hasUnstoredVolumes(Long bindBatchId) {
        Long count = bindVolumeMapper.selectCount(new LambdaQueryWrapper<BindVolume>()
            .eq(BindVolume::getBindBatchId, bindBatchId)
            .eq(BindVolume::getDeleteFlag, "N")
            .ne(BindVolume::getBindStatus, "STORED"));
        return count != null && count > 0;
    }
    private ArchiveCreateSession requireSession(String sessionCode) {
        ArchiveCreateSession session = archiveCreateSessionMapper.selectOne(new LambdaQueryWrapper<ArchiveCreateSession>().eq(ArchiveCreateSession::getSessionCode, sessionCode).last("limit 1"));
        if (session == null) {
            throw new BusinessException("Create session does not exist");
        }
        return session;
    }

    private DocumentType requireDocumentType(String documentTypeCode) {
        DocumentType type = documentTypeMapper.selectOne(new LambdaQueryWrapper<DocumentType>().eq(DocumentType::getTypeCode, documentTypeCode).eq(DocumentType::getDeleteFlag, "N").eq(DocumentType::getEnabledFlag, "Y").last("limit 1"));
        if (type == null) {
            throw new BusinessException("Document type does not exist or is disabled");
        }
        return type;
    }

    private CompanyProject requireCompanyProject(String companyProjectCode) {
        CompanyProject project = companyProjectMapper.selectOne(new LambdaQueryWrapper<CompanyProject>().eq(CompanyProject::getCompanyProjectCode, companyProjectCode).eq(CompanyProject::getDeleteFlag, "N").eq(CompanyProject::getEnabledFlag, "Y").last("limit 1"));
        if (project == null) {
            throw new BusinessException("Company/project does not exist or is disabled");
        }
        return project;
    }

    private int scoreRule(ArchiveFlowRule rule, String customRule, String archiveDestination) {
        int score = 0;
        if (Objects.equals(trimToNull(rule.getCustomRule()), trimToNull(customRule))) score += 2;
        if (Objects.equals(trimToNull(rule.getArchiveDestination()), trimToNull(archiveDestination))) score += 2;
        if (!StringUtils.hasText(rule.getCustomRule())) score += 1;
        if (!StringUtils.hasText(rule.getArchiveDestination())) score += 1;
        return score;
    }

    private void validateRequired(ArchiveCreateCommand command) {
        requireText(command.getDocumentTypeCode(), "documentTypeCode");
        requireText(command.getCompanyProjectCode(), "companyProjectCode");
        requireText(command.getBeginPeriod(), "beginPeriod");
        requireText(command.getEndPeriod(), "endPeriod");
        requireText(command.getDocumentName(), "documentName");
        requireText(command.getDutyPerson(), "dutyPerson");
        requireText(command.getDutyDepartment(), "dutyDepartment");
        if (command.getDocumentDate() == null) throw new BusinessException("documentDate is required");
        requireText(command.getSecurityLevelCode(), "securityLevelCode");
        requireText(command.getCarrierTypeCode(), "carrierTypeCode");
        requireText(command.getDocumentOrganizationCode(), "documentOrganizationCode");
        if (command.getRetentionPeriodYears() == null) throw new BusinessException("retentionPeriodYears is required");
        requireText(command.getArchiveTypeCode(), "archiveTypeCode");
    }

    private void validateExtValues(List<DocumentTypeExtFieldResponse> fields, Map<String, String> extValues) {
        Map<String, String> safeValues = extValues == null ? Map.of() : extValues;
        for (DocumentTypeExtFieldResponse field : fields) {
            if ("Y".equals(field.getRequiredFlag()) && !StringUtils.hasText(safeValues.get(field.getFieldCode()))) {
                throw new BusinessException(field.getFieldName() + " is required");
            }
        }
    }

    private boolean requiresElectronic(String carrierTypeCode) { return List.of("ELECTRONIC", "HYBRID").contains(normalizeCarrierType(carrierTypeCode)); }
    private boolean requiresPaper(String carrierTypeCode) { return List.of("PAPER", "HYBRID").contains(normalizeCarrierType(carrierTypeCode)); }

    private void persistExtValues(Long archiveId, List<DocumentTypeExtFieldResponse> fields, Map<String, String> extValues) {
        if (extValues == null || extValues.isEmpty()) return;
        Map<String, DocumentTypeExtFieldResponse> fieldMap = fields.stream().collect(Collectors.toMap(DocumentTypeExtFieldResponse::getFieldCode, Function.identity(), (left, right) -> left));
        extValues.forEach((fieldCode, value) -> {
            if (!StringUtils.hasText(value) || !fieldMap.containsKey(fieldCode)) return;
            DocumentTypeExtFieldResponse field = fieldMap.get(fieldCode);
            ArchiveExtValue entity = new ArchiveExtValue();
            entity.setArchiveId(archiveId);
            entity.setFieldCode(fieldCode);
            entity.setFieldNameSnapshot(field.getFieldName());
            entity.setFieldType(field.getFieldType());
            entity.setDictCategoryCode(field.getDictCategoryCode());
            if ("DICT".equals(field.getFieldType())) {
                entity.setDictItemCode(value.trim());
                entity.setDictItemNameSnapshot(value.trim());
            } else {
                entity.setTextValue(value.trim());
            }
            entity.setValueSource("MANUAL");
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            archiveExtValueMapper.insert(entity);
        });
    }

    private Map<String, String> resolveExtValues(Map<String, String> submittedValues,
                                                 List<DocumentTypeExtFieldResponse> fields,
                                                 ParsedAttachment combinedParse) {
        Map<String, String> resolved = new LinkedHashMap<>();
        if (submittedValues != null) {
            submittedValues.forEach((key, value) -> {
                if (StringUtils.hasText(value)) {
                    resolved.put(key, value.trim());
                }
            });
        }
        if (fields == null || fields.isEmpty()) {
            return resolved;
        }
        String descriptionFallback = combinedParse.extendedValues().getOrDefault("description", combinedParse.preview());
        for (DocumentTypeExtFieldResponse field : fields) {
            if (resolved.containsKey(field.getFieldCode())) {
                continue;
            }
            String directValue = combinedParse.extendedValues().get(field.getFieldCode());
            if (StringUtils.hasText(directValue)) {
                resolved.put(field.getFieldCode(), directValue.trim());
                continue;
            }
            if ("TEXT".equals(field.getFieldType()) && StringUtils.hasText(descriptionFallback)) {
                resolved.put(field.getFieldCode(), descriptionFallback.trim());
            }
        }
        return resolved;
    }

    private void persistContentAndVectors(Long archiveId, ArchiveAttachment attachment, ParsedAttachment parsed) {
        if (!parsed.hasText()) return;
        ArchiveContent content = new ArchiveContent();
        content.setArchiveId(archiveId);
        content.setAttachmentId(attachment.getAttachmentId());
        content.setContentVersion(attachment.getVersionNo());
        content.setFullText(parsed.fullText());
        content.setTextLength(parsed.fullText().length());
        content.setParseTime(LocalDateTime.now());
        content.setOcrFlag(parsed.ocrEnhanced() ? "Y" : "N");
        content.setDeleteFlag("N");
        content.setCreatedBy(SYSTEM_OPERATOR_ID);
        content.setCreationDate(LocalDateTime.now());
        content.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        content.setLastUpdateDate(LocalDateTime.now());
        archiveContentMapper.insert(content);

        List<String> chunks = archiveTextChunkService.chunk(parsed.fullText());
        String embeddingModelCode = findEmbeddingModelCode();
        for (int index = 0; index < chunks.size(); index++) {
            String chunkText = chunks.get(index);
            ArchiveContentChunk chunk = new ArchiveContentChunk();
            chunk.setContentId(content.getContentId());
            chunk.setArchiveId(archiveId);
            chunk.setAttachmentId(attachment.getAttachmentId());
            chunk.setChunkNo(index + 1);
            chunk.setChunkText(chunkText);
            chunk.setTokenCount(chunkText.length());
            chunk.setPositionStart(index == 0 ? 0 : Math.max(0, index * 420));
            chunk.setPositionEnd(chunk.getPositionStart() + chunkText.length());
            chunk.setDeleteFlag("N");
            chunk.setCreatedBy(SYSTEM_OPERATOR_ID);
            chunk.setCreationDate(LocalDateTime.now());
            chunk.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            chunk.setLastUpdateDate(LocalDateTime.now());
            archiveContentChunkMapper.insert(chunk);

            var vector = archiveTextVectorService.embed(chunkText);
            jdbcTemplate.update(
                "insert into arc_archive_chunk_vector (chunk_id, archive_id, attachment_id, embedding_model_code, vector_value, vector_dimension, content_version, delete_flag, created_by, creation_date, last_updated_by, last_update_date) values (?, ?, ?, ?, CAST(? AS vector), ?, ?, 'N', ?, current_timestamp, ?, current_timestamp)",
                chunk.getChunkId(),
                archiveId,
                attachment.getAttachmentId(),
                embeddingModelCode,
                archiveTextVectorService.toPgVectorLiteral(vector),
                archiveTextVectorService.dimension(),
                attachment.getVersionNo(),
                SYSTEM_OPERATOR_ID,
                SYSTEM_OPERATOR_ID
            );
        }
    }

    private ArchiveCreateSessionResponse buildSessionResponse(ArchiveCreateSession session, List<ArchiveAttachment> attachments, ArchiveAiParseResult parseResult) {
        return ArchiveCreateSessionResponse.builder().sessionId(session.getSessionId()).sessionCode(session.getSessionCode()).createMode(session.getCreateMode()).sessionStatus(session.getSessionStatus()).documentTypeCodeGuess(session.getDocumentTypeCodeGuess()).carrierTypeCodeGuess(session.getCarrierTypeCodeGuess()).parseStatus(session.getParseStatus()).aiSummarySnapshot(session.getAiSummarySnapshot()).expireTime(session.getExpireTime()).attachments(attachments.stream().map(this::toAttachmentResponse).toList()).aiParseResult(parseResult).build();
    }

    private ArchiveSummaryResponse buildArchiveSummary(ArchiveRecord archive, Map<String, String> extValues, List<ArchiveAttachment> attachments) {
        Map<String, String> typeNameMap = documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>().eq(DocumentType::getDeleteFlag, "N")).stream().collect(Collectors.toMap(DocumentType::getTypeCode, DocumentType::getTypeName, (left, right) -> left));
        Map<String, String> companyNameMap = companyProjectMapper.selectList(new LambdaQueryWrapper<CompanyProject>().eq(CompanyProject::getDeleteFlag, "N")).stream().collect(Collectors.toMap(CompanyProject::getCompanyProjectCode, CompanyProject::getCompanyProjectName, (left, right) -> left));
        return ArchiveSummaryResponse.builder().archiveId(archive.getArchiveId()).archiveCode(archive.getArchiveCode()).archiveFilingCode(archive.getArchiveFilingCode()).documentTypeCode(archive.getDocumentTypeCode()).documentTypeName(typeNameMap.getOrDefault(archive.getDocumentTypeCode(), archive.getDocumentTypeCode())).companyProjectCode(archive.getCompanyProjectCode()).companyProjectName(companyNameMap.getOrDefault(archive.getCompanyProjectCode(), archive.getCompanyProjectCode())).documentName(archive.getDocumentName()).businessCode(archive.getBusinessCode()).dutyPerson(archive.getDutyPerson()).dutyDepartment(archive.getDutyDepartment()).documentDate(archive.getDocumentDate()).securityLevelCode(archive.getSecurityLevelCode()).sourceSystem(archive.getSourceSystem()).archiveDestination(archive.getArchiveDestination()).originPlace(archive.getOriginPlace()).carrierTypeCode(archive.getCarrierTypeCode()).aiArchiveSummary(archive.getAiArchiveSummary()).documentOrganizationCode(archive.getDocumentOrganizationCode()).retentionPeriodYears(archive.getRetentionPeriodYears()).archiveTypeCode(archive.getArchiveTypeCode()).archiveStatus(archive.getArchiveStatus()).parseStatus(archive.getParseStatus()).vectorStatus(archive.getVectorStatus()).lastUpdateDate(archive.getLastUpdateDate()).attachmentCount(attachments.size()).extValues(extValues).attachments(attachments.stream().map(this::toAttachmentResponse).toList()).build();
    }

    private List<ArchiveAttachment> listSessionAttachments(Long sessionId) {
        return archiveAttachmentMapper.selectList(new LambdaQueryWrapper<ArchiveAttachment>().eq(ArchiveAttachment::getSessionId, sessionId).eq(ArchiveAttachment::getDeleteFlag, "N").orderByAsc(ArchiveAttachment::getAttachmentSeq));
    }

    private Map<Long, List<ArchiveAttachment>> loadAttachmentMap(List<Long> archiveIds) {
        if (archiveIds == null || archiveIds.isEmpty()) return Map.of();
        return archiveAttachmentMapper.selectList(new LambdaQueryWrapper<ArchiveAttachment>().in(ArchiveAttachment::getArchiveId, archiveIds).eq(ArchiveAttachment::getDeleteFlag, "N").orderByAsc(ArchiveAttachment::getAttachmentSeq)).stream().collect(Collectors.groupingBy(ArchiveAttachment::getArchiveId, LinkedHashMap::new, Collectors.toList()));
    }

    private Set<Long> collectInMemoryMatchedArchiveIds(
        List<ArchiveRecord> allRecords,
        Map<Long, Map<String, String>> extValueMap,
        Map<Long, List<ArchiveAttachment>> archiveAttachmentMap,
        List<String> searchTerms
    ) {
        Set<Long> matchedIds = new HashSet<>();
        for (ArchiveRecord record : allRecords) {
            Long archiveId = record.getArchiveId();
            // 检查档案记录本身的字段
            if (matchesSearchTerms(record, searchTerms)) {
                matchedIds.add(archiveId);
                continue;
            }
            // 检查扩展字段
            Map<String, String> extValues = extValueMap.get(archiveId);
            if (extValues != null && matchesSearchTerms(extValues, searchTerms)) {
                matchedIds.add(archiveId);
                continue;
            }
            // 检查附件
            List<ArchiveAttachment> attachments = archiveAttachmentMap.get(archiveId);
            if (attachments != null && matchesSearchTerms(attachments, searchTerms)) {
                matchedIds.add(archiveId);
            }
        }
        return matchedIds;
    }

    private boolean matchesSearchTerms(ArchiveRecord record, List<String> searchTerms) {
        String content = record.getDocumentName() + " " + record.getBusinessCode() + " " + record.getRemark();
        return searchTerms.stream().anyMatch(term -> content.toLowerCase().contains(term.toLowerCase()));
    }

    private boolean matchesSearchTerms(Map<String, String> extValues, List<String> searchTerms) {
        String content = extValues.values().stream().collect(Collectors.joining(" "));
        return searchTerms.stream().anyMatch(term -> content.toLowerCase().contains(term.toLowerCase()));
    }

    private boolean matchesSearchTerms(List<ArchiveAttachment> attachments, List<String> searchTerms) {
        String content = attachments.stream()
            .map(attachment -> attachment.getFileName() + " " + attachment.getRemark() + " " + attachment.getAiSummary())
            .collect(Collectors.joining(" "));
        return searchTerms.stream().anyMatch(term -> content.toLowerCase().contains(term.toLowerCase()));
    }

    private Map<Long, Map<String, String>> loadExtValueMap(List<Long> archiveIds) {
        if (archiveIds == null || archiveIds.isEmpty()) return Map.of();
        return archiveExtValueMapper.selectList(new LambdaQueryWrapper<ArchiveExtValue>().in(ArchiveExtValue::getArchiveId, archiveIds)).stream().collect(Collectors.groupingBy(ArchiveExtValue::getArchiveId, LinkedHashMap::new, Collectors.toMap(ArchiveExtValue::getFieldCode, this::resolveExtValue, (left, right) -> right, LinkedHashMap::new)));
    }

    private boolean matchesExtFilters(Map<String, String> extValues, Map<String, String> filters) {
        return filters.entrySet().stream().filter(entry -> StringUtils.hasText(entry.getValue())).allMatch(entry -> containsIgnoreCase(extValues.get(entry.getKey()), entry.getValue().trim().toLowerCase(Locale.ROOT)));
    }

    private List<String> buildKeywordSearchTerms(String keyword) {
        String trimmed = trimToNull(keyword);
        if (trimmed == null) return List.of();
        LinkedHashSet<String> terms = new LinkedHashSet<>();
        terms.add(trimmed);

        String normalized = trimmed
            .replaceAll("[，。、“”‘’：:；;,.!?！？（）()【】\\[\\]{}]", " ")
            .replaceAll("\\s+", " ")
            .trim();
        if (StringUtils.hasText(normalized)) {
            terms.add(normalized);
        }

        String simplified = normalized;
        for (String filler : List.of("相关文档", "相关资料", "相关文件", "文档", "资料", "文件", "内容", "查询", "搜索", "检索", "查找", "查一下", "搜一下", "看一下", "给我", "帮我", "关于", "有哪些", "有吗", "有没有", "是什么", "怎么", "如何")) {
            simplified = simplified.replace(filler, " ");
        }
        simplified = simplified.replaceAll("\\s+", " ").trim();
        if (StringUtils.hasText(simplified) && simplified.length() >= 2) {
            terms.add(simplified);
            Arrays.stream(simplified.split(" "))
                .map(String::trim)
                .filter(item -> item.length() >= 2)
                .forEach(terms::add);
        }
        return terms.stream().limit(8).toList();
    }

    private List<String> buildNormalizedKeywordSearchTerms(String keyword) {
        String trimmed = trimToNull(keyword);
        if (trimmed == null) return List.of();
        LinkedHashSet<String> terms = new LinkedHashSet<>();
        terms.add(trimmed);

        String normalized = trimmed
            .replaceAll("[,.;:!?()\\[\\]{}<>\"'`~|/\\\\_-]+", " ")
            .replaceAll("\\s+", " ")
            .trim();
        if (StringUtils.hasText(normalized)) {
            terms.add(normalized);
        }

        String simplified = normalized;
        for (String filler : List.of(
            "相关文档", "相关资料", "相关文件", "文档", "资料", "文件", "内容",
            "查询", "搜索", "检索", "查找", "查一个", "搜一个", "看一个",
            "给我", "帮我", "关于", "有哪些", "有吗", "有没有", "是什么", "怎么", "如何",
            "related", "document", "documents", "file", "files", "search", "query"
        )) {
            simplified = simplified.replace(filler, " ");
        }
        simplified = simplified.replaceAll("\\s+", " ").trim();
        if (StringUtils.hasText(simplified) && simplified.length() >= 2) {
            terms.add(simplified);
            Arrays.stream(simplified.split(" "))
                .map(String::trim)
                .filter(item -> item.length() >= 2)
                .filter(item -> !isNoiseSearchToken(item))
                .forEach(terms::add);
        }
        return terms.stream().limit(8).toList();
    }

    private Set<Long> loadKeywordMatchedArchiveIds(List<String> searchTerms) {
        if (searchTerms.isEmpty()) return Set.of();
        Set<Long> matchedArchiveIds = new LinkedHashSet<>();
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive_content where delete_flag = 'N' and (%s)",
            "full_text",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive_attachment where delete_flag = 'N' and active_flag = 'Y' and (%s)",
            "file_name",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive_attachment where delete_flag = 'N' and active_flag = 'Y' and (%s)",
            "ai_summary",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "document_name",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "ai_archive_summary",
            searchTerms
        ));
        return matchedArchiveIds;
    }

    private Set<Long> loadUnifiedKeywordMatchedArchiveIds(List<String> searchTerms) {
        if (searchTerms.isEmpty()) return Set.of();
        Set<Long> matchedArchiveIds = new LinkedHashSet<>();
        matchedArchiveIds.addAll(loadKeywordMatchedArchiveIds(searchTerms));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "business_code",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "archive_filing_code",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "duty_department",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive where delete_flag = 'N' and (%s)",
            "source_system",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive_ext_value where (%s)",
            "text_value",
            searchTerms
        ));
        matchedArchiveIds.addAll(queryArchiveIdsByTerms(
            "select distinct archive_id from arc_archive_ext_value where (%s)",
            "dict_item_name_snapshot",
            searchTerms
        ));
        return matchedArchiveIds;
    }

    private Set<Long> queryArchiveIdsByTerms(String sqlTemplate, String columnName, List<String> searchTerms) {
        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder();
        for (int index = 0; index < searchTerms.size(); index++) {
            if (index > 0) where.append(" or ");
            where.append(columnName).append(" ilike ?");
            params.add("%" + searchTerms.get(index) + "%");
        }
        String sql = sqlTemplate.formatted(where);
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong(1), params.toArray()));
    }

    private Set<Long> loadContentMatchedArchiveIds(List<String> searchTerms) {
        if (searchTerms.isEmpty()) return Set.of();
        StringBuilder sql = new StringBuilder("select distinct archive_id from arc_archive_content where delete_flag = 'N' and (");
        List<Object> params = new ArrayList<>();
        for (int index = 0; index < searchTerms.size(); index++) {
            if (index > 0) sql.append(" or ");
            sql.append("full_text ilike ?");
            params.add("%" + searchTerms.get(index) + "%");
        }
        sql.append(")");
        return new LinkedHashSet<>(jdbcTemplate.query(sql.toString(), (rs, rowNum) -> rs.getLong(1), params.toArray()));
    }

    private boolean matchesKeyword(ArchiveRecord record, List<String> searchTerms) {
        return searchTerms.stream().anyMatch(term -> {
            String lowerTerm = term.toLowerCase(Locale.ROOT);
            return containsIgnoreCase(record.getDocumentName(), lowerTerm)
                || containsIgnoreCase(record.getBusinessCode(), lowerTerm)
                || containsIgnoreCase(record.getArchiveFilingCode(), lowerTerm)
                || containsIgnoreCase(record.getAiArchiveSummary(), lowerTerm)
                || containsIgnoreCase(record.getDutyDepartment(), lowerTerm)
                || containsIgnoreCase(record.getSourceSystem(), lowerTerm);
        });
    }

    private boolean matchesKeywordInValues(Map<String, String> extValues, List<String> searchTerms) {
        return extValues.values().stream()
            .filter(StringUtils::hasText)
            .anyMatch(value -> searchTerms.stream().anyMatch(term -> containsIgnoreCase(value, term.toLowerCase(Locale.ROOT))));
    }

    private boolean matchesKeywordInAttachments(List<ArchiveAttachment> attachments, List<String> searchTerms) {
        return attachments.stream().anyMatch(attachment -> searchTerms.stream().anyMatch(term -> {
            String lowerTerm = term.toLowerCase(Locale.ROOT);
            return containsIgnoreCase(attachment.getFileName(), lowerTerm)
                || containsIgnoreCase(attachment.getAiSummary(), lowerTerm)
                || containsIgnoreCase(attachment.getRemark(), lowerTerm);
        }));
    }

    private boolean isNoiseSearchToken(String token) {
        return Set.of("doc", "docx", "pdf", "txt", "xls", "xlsx", "ppt", "pptx")
            .contains(token.toLowerCase(Locale.ROOT));
    }

    private String resolveExtValue(ArchiveExtValue item) { return "DICT".equals(item.getFieldType()) ? item.getDictItemNameSnapshot() : item.getTextValue(); }
    private ArchiveAttachmentResponse toAttachmentResponse(ArchiveAttachment attachment) {
        return ArchiveAttachmentResponse.builder()
            .attachmentId(attachment.getAttachmentId())
            .attachmentRole(attachment.getAttachmentRole())
            .attachmentTypeCode(attachment.getAttachmentTypeCode())
            .attachmentSeq(attachment.getAttachmentSeq())
            .versionNo(attachment.getVersionNo())
            .fileName(attachment.getFileName())
            .mimeType(attachment.getMimeType())
            .fileSize(attachment.getFileSize())
            .remark(attachment.getRemark())
            .aiSummary(attachment.getAiSummary())
            .parseStatus(attachment.getParseStatus())
            .vectorStatus(attachment.getVectorStatus())
            .creationDate(attachment.getCreationDate())
            .build();
    }

    private ParsedAttachment parseStoredFile(Path storagePath, String originalFilename, String mimeType) {
        try {
            var extracted = archiveFileTextExtractor.extract(storagePath, originalFilename, mimeType);
            String text = extracted.text();
            MetadataFallback fallback = inferMetadataFallback(storagePath, originalFilename, text);
            CompanyProjectMatch companyProjectMatch = matchCompanyProject(fallback.companyName(), fallback.projectName());
            String effectiveText = StringUtils.hasText(text) ? text : buildFallbackText(originalFilename, fallback);
            return new ParsedAttachment(
                effectiveText,
                summarizeText(originalFilename, effectiveText),
                guessDocumentTypeCode(originalFilename, effectiveText, fallback.docType()),
                guessBusinessCode(effectiveText),
                companyProjectMatch.companyProjectCode(),
                companyProjectMatch.companyProjectName(),
                fallback.beginPeriod(),
                fallback.endPeriod(),
                fallback.documentDate(),
                buildParseExplain(fallback, companyProjectMatch),
                StringUtils.hasText(text) ? 0.90d : 0.35d,
                buildExtendedValues(fallback),
                extracted.ocrEnhanced()
            );
        } catch (IOException exception) {
            return new ParsedAttachment(
                "",
                stripExtension(originalFilename) + " 上传成功，但正文暂未解析出来，请人工补充信息或稍后重试。",
                null,
                null,
                null,
                null,
                null,
                null,
                readFileCreationDate(storagePath),
                "正文解析失败，文档生成日期已使用文件属性创建时间兜底",
                0.20d,
                Map.of(),
                false
            );
        }
    }

    private ParsedAttachment buildCombinedParseResult(List<ArchiveAttachment> attachments, String documentTypeCode) {
        String allText = attachments.stream()
            .map(item -> parseStoredFile(Paths.get(item.getStoragePath()), item.getFileName(), item.getMimeType()).fullText())
            .filter(StringUtils::hasText)
            .collect(Collectors.joining("\n"));
        return new ParsedAttachment(
            allText,
            summarizeText(documentTypeCode, allText),
            documentTypeCode,
            guessBusinessCode(allText),
            null,
            null,
            extractBeginPeriod(allText),
            extractEndPeriod(allText),
            extractDocumentDate(allText),
            null,
            StringUtils.hasText(allText) ? 0.92d : 0.30d,
            Map.of(),
            attachments.stream().anyMatch(item -> "SUCCESS".equalsIgnoreCase(item.getParseStatus()))
        );
    }

    private ArchiveAiParseResult buildParseResult(ArchiveCreateSession session, ArchiveAttachment attachment) {
        ParsedAttachment parsed = parseStoredFile(Paths.get(attachment.getStoragePath()), attachment.getFileName(), attachment.getMimeType());
        return ArchiveAiParseResult.builder()
            .suggestedDocumentTypeCode(Optional.ofNullable(session.getDocumentTypeCodeGuess()).orElse(parsed.suggestedDocumentTypeCode()))
            .suggestedCarrierTypeCode(Optional.ofNullable(session.getCarrierTypeCodeGuess()).orElse("ELECTRONIC"))
            .documentName(stripExtension(attachment.getFileName()))
            .businessCode(parsed.businessCode())
            .companyProjectCode(parsed.companyProjectCode())
            .companyProjectName(parsed.companyProjectName())
            .beginPeriod(parsed.beginPeriod())
            .endPeriod(parsed.endPeriod())
            .documentDate(parsed.documentDate())
            .sourceSystem("AI_UPLOAD")
            .aiSummary(parsed.summary())
            .extractedTextPreview(parsed.preview())
            .confidence(parsed.confidence())
            .parseExplain(parsed.parseExplain())
            .extendedValues(parsed.extendedValues())
            .build();
    }

    private String summarizeText(String title, String text) {
        if (!StringUtils.hasText(text)) {
            return "未提取到可用正文，系统已保留文件并等待人工补录或后续重试。";
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        String excerpt = normalized.length() > 180 ? normalized.substring(0, 180) + "..." : normalized;
        return "《" + stripExtension(title) + "》内容摘要：" + excerpt;
    }

    private String guessDocumentTypeCode(String filename, String text, String fallbackDocType) {
        String combined = (filename + " " + Objects.toString(text, "") + " " + Objects.toString(fallbackDocType, "")).toLowerCase(Locale.ROOT);
        return documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>()
                .eq(DocumentType::getDeleteFlag, "N")
                .eq(DocumentType::getEnabledFlag, "Y"))
            .stream()
            .filter(item -> combined.contains(item.getTypeName().toLowerCase(Locale.ROOT)) || combined.contains(item.getTypeCode().toLowerCase(Locale.ROOT)))
            .sorted(Comparator.comparing(DocumentType::getLevelNum).reversed())
            .map(DocumentType::getTypeCode)
            .findFirst()
            .orElse(null);
    }

    private String guessBusinessCode(String text) {
        if (!StringUtils.hasText(text)) return null;
        Matcher matcher = BUSINESS_CODE_PATTERN.matcher(text.toUpperCase(Locale.ROOT));
        return matcher.find() ? matcher.group() : null;
    }

    private MetadataFallback inferMetadataFallback(Path storagePath, String fileName, String fullText) {
        String safeFileName = fileName == null ? "" : fileName.trim();
        String content = fullText == null ? "" : fullText.trim();
        String baseFileName = stripExtension(safeFileName).trim();
        String firstLine = firstMeaningfulLine(content);
        String personName = extractPersonName(content);
        boolean resume = safeFileName.contains("简历")
            || content.contains("个人简历")
            || safeFileName.toUpperCase(Locale.ROOT).startsWith("JL-");
        if (resume) {
            String description = StringUtils.hasText(personName) ? personName + " 的简历档案" : "候选人简历档案";
            String projectName = StringUtils.hasText(personName) ? personName + " 简历" : null;
            String resumePeriod = extractPeriod(content);
            String documentDate = extractDocumentDate(content);
            if (!StringUtils.hasText(documentDate)) {
                documentDate = readFileCreationDate(storagePath);
            }
            return new MetadataFallback("招聘输入", "简历", null, null, projectName, resumePeriod, resumePeriod, documentDate, description);
        }

        String combined = String.join(" ", safeFileName, firstLine, content.substring(0, Math.min(content.length(), 200)));
        String docType = inferDocType(combined, firstLine);
        String beginPeriod = extractBeginPeriod(content);
        String endPeriod = extractEndPeriod(content);
        String documentDate = extractDocumentDate(content);
        if (!StringUtils.hasText(documentDate)) {
            documentDate = extractDocumentDateFromFileName(safeFileName);
        }
        if (!StringUtils.hasText(documentDate)) {
            documentDate = readFileCreationDate(storagePath);
        }
        String companyName = extractCompanyCandidate(safeFileName, content);
        String description = buildDescription(firstLine, content);
        return new MetadataFallback(
            StringUtils.hasText(baseFileName) ? baseFileName : null,
            docType,
            companyName,
            null,
            companyName,
            beginPeriod,
            endPeriod,
            documentDate,
            description
        );
    }

    private MetadataFallback inferMetadataFallback(String fileName, String fullText) {
        if (fileName != null || fullText != null) {
            return inferMetadataFallback((Path) null, fileName, fullText);
        }
        String safeFileName = fileName == null ? "" : fileName.trim();
        String content = fullText == null ? "" : fullText.trim();
        String baseFileName = stripExtension(safeFileName).trim();
        String firstLine = firstMeaningfulLine(content);
        String personName = extractPersonName(content);
        boolean resume = safeFileName.contains("简历")
            || content.contains("个人简历")
            || safeFileName.toUpperCase(Locale.ROOT).startsWith("JL-");
        if (resume) {
            String description = StringUtils.hasText(personName) ? personName + " 的简历档案" : "候选人简历档案";
            String projectName = StringUtils.hasText(personName) ? personName + " 简历" : null;
            return new MetadataFallback("招聘输入", "简历", null, null, projectName, extractPeriod(content), description);
        }
        String combined = String.join(" ", safeFileName, firstLine, content.substring(0, Math.min(content.length(), 200)));
        String docType = inferDocType(combined, firstLine);
        String periodValue = extractPeriod(content);
        String description = buildDescription(firstLine, content);
        return new MetadataFallback(StringUtils.hasText(baseFileName) ? baseFileName : null, docType, null, null, null, periodValue, description);
    }

    private String inferDocType(String combined, String firstLine) {
        String normalized = combined == null ? "" : combined.toLowerCase(Locale.ROOT);
        if (normalized.contains("简历") || normalized.contains("resume") || normalized.startsWith("jl-")) return "简历";
        if (normalized.contains("报告") || normalized.contains("report") || normalized.contains("体检")) return "报告";
        if (normalized.contains("通知") || normalized.contains("公告") || normalized.contains("意见")) return "通知";
        if (normalized.contains("合同") || normalized.contains("协议")) return "合同";
        if (normalized.contains("发票") || normalized.contains("invoice")) return "发票";
        if (normalized.contains("清单") || normalized.contains("目录") || normalized.contains("确认表")) return "清单";
        if (!isBlank(firstLine) && firstLine.length() <= 30) return firstLine;
        return null;
    }

    private String extractPeriod(String text) {
        Matcher matcher = PERIOD_PATTERN.matcher(text == null ? "" : text);
        if (!matcher.find()) {
            return null;
        }
        return formatYearMonth(matcher.group(1), matcher.group(2));
    }

    private String extractBeginPeriod(String text) {
        Matcher matcher = DATE_RANGE_PATTERN.matcher(text == null ? "" : text);
        if (matcher.find()) {
            return formatYearMonth(matcher.group(1), matcher.group(2));
        }
        return extractPeriod(text);
    }

    private String extractEndPeriod(String text) {
        Matcher matcher = DATE_RANGE_PATTERN.matcher(text == null ? "" : text);
        if (matcher.find()) {
            return formatYearMonth(matcher.group(4), matcher.group(5));
        }
        return extractPeriod(text);
    }

    private String extractDocumentDate(String text) {
        String normalized = text == null ? "" : text;
        Matcher keywordMatcher = Pattern.compile("(?:签订日期|签署日期|文档日期|成文日期|落款日期|日期)[：: ]{0,4}(20\\d{2})[年\\-./](\\d{1,2})(?:[月\\-./](\\d{1,2}))?日?").matcher(normalized);
        if (keywordMatcher.find()) {
            return formatDate(keywordMatcher.group(1), keywordMatcher.group(2), keywordMatcher.group(3));
        }
        return null;
    }

    private String extractDocumentDateFromFileName(String fileName) {
        Matcher matcher = FILE_NAME_DATE_PATTERN.matcher(fileName == null ? "" : fileName);
        if (!matcher.find()) {
            return null;
        }
        return formatDate(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    private String readFileCreationDate(Path storagePath) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(storagePath, BasicFileAttributes.class);
            return attributes.creationTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString();
        } catch (IOException exception) {
            return null;
        }
    }

    private String extractCompanyCandidate(String fileName, String text) {
        Matcher matcher = COMPANY_IN_BRACKETS_PATTERN.matcher(fileName == null ? "" : fileName);
        if (matcher.find()) {
            String candidate = normalizeCompanyToken(matcher.group(1));
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }
        String normalizedText = text == null ? "" : text;
        Matcher buyerMatcher = Pattern.compile("(?:甲方|采购方|买方)[：: ]{0,3}([A-Za-z0-9\\p{IsHan}\\-（）() ]{2,40})").matcher(normalizedText);
        if (buyerMatcher.find()) {
            return normalizeCompanyToken(buyerMatcher.group(1));
        }
        Matcher partyMatcher = Pattern.compile("(?:乙方|供应商|卖方)[：: ]{0,3}([A-Za-z0-9\\p{IsHan}\\-（）() ]{2,40})").matcher(normalizedText);
        if (partyMatcher.find()) {
            return normalizeCompanyToken(partyMatcher.group(1));
        }
        return null;
    }

    private CompanyProjectMatch matchCompanyProject(String... candidates) {
        List<CompanyProject> projects = companyProjectMapper.selectList(new LambdaQueryWrapper<CompanyProject>()
            .eq(CompanyProject::getDeleteFlag, "N")
            .eq(CompanyProject::getEnabledFlag, "Y"));
        List<CompanyProject> matched = new ArrayList<>();
        for (String candidate : candidates) {
            String normalizedCandidate = normalizeCompanyToken(candidate);
            if (!StringUtils.hasText(normalizedCandidate)) {
                continue;
            }
            List<CompanyProject> currentMatches = projects.stream()
                .filter(project -> normalizedCandidate.equals(normalizeCompanyToken(project.getCompanyProjectName()))
                    || normalizedCandidate.equals(normalizeCompanyToken(project.getCompanyProjectCode())))
                .toList();
            if (currentMatches.size() == 1) {
                CompanyProject project = currentMatches.get(0);
                return new CompanyProjectMatch(project.getCompanyProjectCode(), project.getCompanyProjectName(), normalizedCandidate);
            }
            matched.addAll(currentMatches);
        }
        if (matched.size() == 1) {
            CompanyProject project = matched.get(0);
            return new CompanyProjectMatch(project.getCompanyProjectCode(), project.getCompanyProjectName(), normalizeCompanyToken(project.getCompanyProjectName()));
        }
        String candidate = Arrays.stream(candidates)
            .map(this::normalizeCompanyToken)
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(null);
        return new CompanyProjectMatch(null, null, candidate);
    }

    private String buildParseExplain(MetadataFallback fallback, CompanyProjectMatch companyProjectMatch) {
        List<String> explain = new ArrayList<>();
        if (StringUtils.hasText(companyProjectMatch.companyProjectCode())) {
            explain.add("公司/项目：匹配自“" + companyProjectMatch.candidate() + "” -> " + companyProjectMatch.companyProjectName());
        } else if (StringUtils.hasText(companyProjectMatch.candidate())) {
            explain.add("公司/项目：识别到“" + companyProjectMatch.candidate() + "”，但未匹配到唯一主数据");
        }
        if (StringUtils.hasText(fallback.beginPeriod()) || StringUtils.hasText(fallback.endPeriod())) {
            explain.add("开始档期/结束档期：根据全文日期归纳");
        }
        if (StringUtils.hasText(fallback.documentDate())) {
            String source = StringUtils.hasText(extractDocumentDate(fallback.description())) ? "正文日期" : "文件属性或文件名";
            explain.add("文档生成日期：来自" + source);
        }
        return explain.isEmpty() ? null : String.join("\n", explain);
    }

    private String normalizeCompanyToken(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value
            .replace('（', '(')
            .replace('）', ')')
            .replaceAll("[()]", "")
            .replaceAll("\\s+", "")
            .toUpperCase(Locale.ROOT)
            .trim();
    }

    private String formatYearMonth(String year, String month) {
        try {
            return YearMonth.of(Integer.parseInt(year), Integer.parseInt(month)).toString();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String formatDate(String year, String month, String day) {
        try {
            int safeDay = StringUtils.hasText(day) ? Integer.parseInt(day) : 1;
            return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), safeDay).toString();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String buildDescription(String firstLine, String fullText) {
        if (!isBlank(firstLine)) {
            return firstLine.length() <= 80 ? firstLine : firstLine.substring(0, 80);
        }
        String normalized = (fullText == null ? "" : fullText).replaceAll("\s+", " ").trim();
        if (normalized.isBlank()) {
            return null;
        }
        return normalized.substring(0, Math.min(normalized.length(), 80));
    }

    private String buildFallbackText(String originalFilename, MetadataFallback fallback) {
        List<String> segments = new ArrayList<>();
        segments.add("文件名：" + stripExtension(originalFilename));
        if (StringUtils.hasText(fallback.docType())) {
            segments.add("推断文档类型：" + fallback.docType());
        }
        if (StringUtils.hasText(fallback.description())) {
            segments.add("摘要：" + fallback.description());
        }
        if (StringUtils.hasText(fallback.periodValue())) {
            segments.add("档期：" + fallback.periodValue());
        }
        return String.join("\n", segments);
    }

    private String extractPersonName(String text) {
        String normalized = text == null ? "" : text;
        Matcher matcher = Pattern.compile("(?:姓名|Name)[:：]?\\s*([\\p{IsHan}A-Za-z·\\s]{2,30})").matcher(normalized);
        boolean matched = matcher.find();
        if (!matched) {
            matcher = PERSON_NAME_PATTERN.matcher(normalized);
            matched = matcher.find();
        }
        if (!matched) {
            return null;
        }
        return matcher.group(1).replaceAll("\\s+", " ").trim();
    }

    private String firstMeaningfulLine(String text) {
        return (text == null ? "" : text).lines().map(String::trim).filter(line -> !line.isEmpty()).findFirst().orElse("");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Map<String, String> buildExtendedValues(MetadataFallback fallback) {
        Map<String, String> result = new LinkedHashMap<>();
        if (StringUtils.hasText(fallback.description())) result.put("description", fallback.description());
        if (StringUtils.hasText(fallback.periodValue())) result.put("periodValue", fallback.periodValue());
        if (StringUtils.hasText(fallback.projectName())) result.put("projectName", fallback.projectName());
        return result;
    }

    private String generateArchiveSummary(ArchiveCreateCommand command, List<ArchiveAttachment> electronicAttachments, List<ArchiveAttachment> paperScanAttachments) {
        List<String> fragments = new ArrayList<>();
        fragments.add("文档名称：" + command.getDocumentName());
        fragments.add("档案类型：" + command.getArchiveTypeCode());
        fragments.add("载体类型：" + normalizeCarrierType(command.getCarrierTypeCode()));
        fragments.add("归档责任部门：" + command.getDutyDepartment());
        if (!electronicAttachments.isEmpty()) {
            fragments.add("电子附件数量：" + electronicAttachments.size());
            fragments.addAll(electronicAttachments.stream().map(ArchiveAttachment::getAiSummary).filter(StringUtils::hasText).limit(2).toList());
        }
        if (!paperScanAttachments.isEmpty()) {
            fragments.add("纸质扫描件数量：" + paperScanAttachments.size());
        }
        return String.join("；", fragments);
    }

    private List<ArchiveSummaryResponse> searchAskReferences(ArchiveAskCommand command, AiModelConfig chatModel) {
        List<ArchiveSummaryResponse> semanticMatches;
        try {
            semanticMatches = findSemanticMatches(command.getQuestion(), command.getDocumentTypeCode(), command.getCompanyProjectCode(), chatModel);
        } catch (Exception exception) {
            semanticMatches = List.of();
        }
        if (!semanticMatches.isEmpty()) return semanticMatches;
        ArchiveQueryCommand queryCommand = new ArchiveQueryCommand();
        queryCommand.setKeyword(command.getQuestion());
        queryCommand.setDocumentTypeCode(command.getDocumentTypeCode());
        queryCommand.setCompanyProjectCode(command.getCompanyProjectCode());
        return queryArchives(queryCommand).getRecords().stream().limit(resolveReferenceLimit(chatModel)).toList();
    }

    private List<ArchiveSummaryResponse> findSemanticMatches(String question, String documentTypeCode, String companyProjectCode, AiModelConfig chatModel) {
        String embeddingModelCode = findEmbeddingModelCode();
        int limit = Math.max(resolveReferenceLimit(chatModel), 3);
        List<Double> vector = archiveTextVectorService.embed(question);
        String normalizedDocumentTypeCode = trimToNull(documentTypeCode);
        String normalizedCompanyProjectCode = trimToNull(companyProjectCode);
        StringBuilder sql = new StringBuilder("""
                select distinct on (a.archive_id)
                    a.archive_id,
                    c.chunk_text,
                    (v.vector_value <=> cast(? as vector)) as distance
                from arc_archive_chunk_vector v
                join arc_archive_content_chunk c on c.chunk_id = v.chunk_id and c.delete_flag = 'N'
                join arc_archive a on a.archive_id = v.archive_id and a.delete_flag = 'N'
                where v.delete_flag = 'N'
                  and v.embedding_model_code = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(archiveTextVectorService.toPgVectorLiteral(vector));
        params.add(embeddingModelCode);
        if (normalizedDocumentTypeCode != null) {
            sql.append(" and a.document_type_code = ?");
            params.add(normalizedDocumentTypeCode);
        }
        if (normalizedCompanyProjectCode != null) {
            sql.append(" and a.company_project_code = ?");
            params.add(normalizedCompanyProjectCode);
        }
        sql.append(" order by a.archive_id, distance asc limit ?");
        params.add(limit);
        List<SemanticMatchRow> rows = jdbcTemplate.query(
            sql.toString(),
            (rs, rowNum) -> new SemanticMatchRow(rs.getLong("archive_id"), rs.getString("chunk_text"), rs.getDouble("distance")),
            params.toArray()
        );
        if (rows.isEmpty()) return List.of();
        List<Long> archiveIds = rows.stream().map(SemanticMatchRow::archiveId).distinct().toList();
        List<ArchiveRecord> archives = archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>().in(ArchiveRecord::getArchiveId, archiveIds).eq(ArchiveRecord::getDeleteFlag, "N"));
        Map<Long, ArchiveRecord> archiveMap = archives.stream().collect(Collectors.toMap(ArchiveRecord::getArchiveId, Function.identity(), (left, right) -> left));
        Map<Long, Map<String, String>> extValueMap = loadExtValueMap(archiveIds);
        Map<Long, List<ArchiveAttachment>> attachmentMap = loadAttachmentMap(archiveIds);
        Map<Long, String> chunkMap = rows.stream().collect(Collectors.toMap(SemanticMatchRow::archiveId, SemanticMatchRow::chunkText, (left, right) -> left));
        return archiveIds.stream()
            .map(archiveMap::get)
            .filter(Objects::nonNull)
            .map(archive -> {
                ArchiveSummaryResponse summary = buildArchiveSummary(archive, extValueMap.getOrDefault(archive.getArchiveId(), Map.of()), attachmentMap.getOrDefault(archive.getArchiveId(), List.of()));
                if (!StringUtils.hasText(summary.getAiArchiveSummary()) && StringUtils.hasText(chunkMap.get(archive.getArchiveId()))) summary.setAiArchiveSummary(chunkMap.get(archive.getArchiveId()));
                return summary;
            })
            .limit(limit)
            .toList();
    }

    private String buildAskEvidenceSnippet(ArchiveSummaryResponse summary) {
        String summaryText = StringUtils.hasText(summary.getAiArchiveSummary()) ? summary.getAiArchiveSummary() : "文档《" + summary.getDocumentName() + "》可作为当前问题的原文依据。";
        String compact = summaryText.replaceAll("\\s+", " ").trim();
        String excerpt = compact.length() > 180 ? compact.substring(0, 180) + "..." : compact;
        return "《" + summary.getDocumentName() + "》：" + excerpt;
    }

    private AiModelConfig findEnabledChatModel() { return aiModelConfigMapper.selectList(new LambdaQueryWrapper<AiModelConfig>().eq(AiModelConfig::getDeleteFlag, "N").eq(AiModelConfig::getEnabledFlag, "Y").eq(AiModelConfig::getModelType, "CHAT").orderByAsc(AiModelConfig::getModelCode)).stream().findFirst().orElse(null); }
    private int resolveReferenceLimit(AiModelConfig chatModel) { if (chatModel == null) return 4; return Math.max(3, Objects.requireNonNullElse(chatModel.getOfficialResultCount(), Objects.requireNonNullElse(chatModel.getTopK(), 4))); }
    private String findEmbeddingModelCode() { return aiModelConfigMapper.selectList(new LambdaQueryWrapper<AiModelConfig>().eq(AiModelConfig::getDeleteFlag, "N").eq(AiModelConfig::getEnabledFlag, "Y").eq(AiModelConfig::getModelType, "EMBEDDING").orderByAsc(AiModelConfig::getModelCode)).stream().map(AiModelConfig::getModelCode).findFirst().orElse("LOCAL_EMBEDDING"); }
    private LabelValueOption option(String code, String name) { return LabelValueOption.builder().code(code).name(name).build(); }
    private String normalizeBindMode(String bindMode) { String mode = requireText(bindMode, "bindMode").toUpperCase(Locale.ROOT); if (!List.of("BUSINESS_CODE", "PERIOD", "MANUAL").contains(mode)) throw new BusinessException("bindMode only supports BUSINESS_CODE, PERIOD, MANUAL"); return mode; }
    private String normalizeStorageSourceType(String sourceType) { String mode = requireText(sourceType, "sourceType").toUpperCase(Locale.ROOT); if (!List.of("BIND_GUIDED", "DIRECT").contains(mode)) throw new BusinessException("sourceType only supports BIND_GUIDED or DIRECT"); return mode; }
    private String normalizeStorageItemType(String itemType) { String type = requireText(itemType, "itemType").toUpperCase(Locale.ROOT); if (!List.of("VOLUME", "ARCHIVE").contains(type)) throw new BusinessException("itemType only supports VOLUME or ARCHIVE"); return type; }
    private String normalizeCreateMode(String createMode) { String mode = requireText(createMode, "createMode").toUpperCase(Locale.ROOT); if (!List.of("AUTO", "MANUAL").contains(mode)) throw new BusinessException("createMode only supports AUTO or MANUAL"); return mode; }
    private String normalizeCarrierType(String carrierTypeCode) { String carrierType = requireText(carrierTypeCode, "carrierTypeCode").toUpperCase(Locale.ROOT); if (!List.of("ELECTRONIC", "PAPER", "HYBRID").contains(carrierType)) throw new BusinessException("carrierTypeCode only supports ELECTRONIC, PAPER, HYBRID"); return carrierType; }
    private String normalizeAttachmentRole(String attachmentRole) { String role = requireText(attachmentRole, "attachmentRole").toUpperCase(Locale.ROOT); if (!List.of("ELECTRONIC", "PAPER_SCAN").contains(role)) throw new BusinessException("attachmentRole only supports ELECTRONIC or PAPER_SCAN"); return role; }
    private String requireText(String value, String fieldName) { if (!StringUtils.hasText(value)) throw new BusinessException(fieldName + " cannot be blank"); return value.trim(); }
    private String inferAttachmentTypeCode(String fileName, String summary, String preview) {
        String combined = (Objects.toString(fileName, "") + " " + Objects.toString(summary, "") + " " + Objects.toString(preview, "")).toLowerCase(Locale.ROOT);
        if (combined.contains("凭证") || combined.contains("voucher") || combined.contains("记账")) {
            return "ACCOUNTING_VOUCHER";
        }
        return StringUtils.hasText(combined) ? "SUPPORTING_ATTACHMENT" : null;
    }
    private String inferAttachmentRemark(String fileName, String summary, String preview) {
        String baseName = stripExtension(fileName);
        if (StringUtils.hasText(summary)) {
            String normalized = summary.replaceAll("\\s+", " ").trim();
            return normalized.length() > 80 ? normalized.substring(0, 80) : normalized;
        }
        if (StringUtils.hasText(preview)) {
            String normalized = preview.replaceAll("\\s+", " ").trim();
            return normalized.length() > 60 ? normalized.substring(0, 60) : normalized;
        }
        return StringUtils.hasText(baseName) ? baseName : null;
    }
    private String trimToNull(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
    private Integer nextAttachmentSeq(Long sessionId) { Long count = archiveAttachmentMapper.selectCount(new LambdaQueryWrapper<ArchiveAttachment>().eq(ArchiveAttachment::getSessionId, sessionId).eq(ArchiveAttachment::getDeleteFlag, "N")); return count == null ? 1 : count.intValue() + 1; }
    private String generateCode(String prefix) { return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT); }
    private String extractExtension(String filename) { if (!StringUtils.hasText(filename) || !filename.contains(".")) return ""; return filename.substring(filename.lastIndexOf('.') + 1); }
    private String stripExtension(String filename) { if (!StringUtils.hasText(filename) || !filename.contains(".")) return filename; return filename.substring(0, filename.lastIndexOf('.')); }
    private String md5Hex(Path path) { try { MessageDigest digest = MessageDigest.getInstance("MD5"); byte[] hashed = digest.digest(Files.readAllBytes(path)); StringBuilder builder = new StringBuilder(); for (byte item : hashed) builder.append(String.format("%02x", item)); return builder.toString(); } catch (IOException | NoSuchAlgorithmException exception) { return UUID.randomUUID().toString().replace("-", ""); } }
    private boolean containsIgnoreCase(String source, String lowerKeyword) { return source != null && source.toLowerCase(Locale.ROOT).contains(lowerKeyword); }
    private List<ArchiveAttachment> mergeAttachments(List<ArchiveAttachment> electronicAttachments, List<ArchiveAttachment> paperScanAttachments) { List<ArchiveAttachment> attachments = new ArrayList<>(); attachments.addAll(electronicAttachments); attachments.addAll(paperScanAttachments); return attachments; }

    private record ParsedAttachment(
        String fullText,
        String summary,
        String suggestedDocumentTypeCode,
        String businessCode,
        String companyProjectCode,
        String companyProjectName,
        String beginPeriod,
        String endPeriod,
        String documentDate,
        String parseExplain,
        Double confidence,
        Map<String, String> extendedValues,
        boolean ocrEnhanced
    ) {
        boolean hasText() { return StringUtils.hasText(fullText); }
        String preview() { if (!StringUtils.hasText(fullText)) return ""; String normalized = fullText.replaceAll("\\s+", " ").trim(); return normalized.length() > 200 ? normalized.substring(0, 200) + "..." : normalized; }
    }

    private record SemanticMatchRow(Long archiveId, String chunkText, Double distance) {
    }

    private record StorageExecutionResult(StorageBatchItemResponse response) {
    }

    private static final class MetadataFallback {
        private final String docSource;
        private final String docType;
        private final String companyName;
        private final String departmentName;
        private final String projectName;
        private final String periodValue;
        private final String beginPeriod;
        private final String endPeriod;
        private final String documentDate;
        private final String description;

        private MetadataFallback(
            String docSource,
            String docType,
            String companyName,
            String departmentName,
            String projectName,
            String periodValue,
            String description
        ) {
            this(docSource, docType, companyName, departmentName, projectName, periodValue, periodValue, periodValue, null, description);
        }

        private MetadataFallback(
            String docSource,
            String docType,
            String companyName,
            String departmentName,
            String projectName,
            String beginPeriod,
            String endPeriod,
            String documentDate,
            String description
        ) {
            this(docSource, docType, companyName, departmentName, projectName, beginPeriod, beginPeriod, endPeriod, documentDate, description);
        }

        private MetadataFallback(
            String docSource,
            String docType,
            String companyName,
            String departmentName,
            String projectName,
            String periodValue,
            String beginPeriod,
            String endPeriod,
            String documentDate,
            String description
        ) {
            this.docSource = docSource;
            this.docType = docType;
            this.companyName = companyName;
            this.departmentName = departmentName;
            this.projectName = projectName;
            this.periodValue = periodValue;
            this.beginPeriod = beginPeriod;
            this.endPeriod = endPeriod;
            this.documentDate = documentDate;
            this.description = description;
        }

        private String docSource() { return docSource; }
        private String docType() { return docType; }
        private String companyName() { return companyName; }
        private String departmentName() { return departmentName; }
        private String projectName() { return projectName; }
        private String periodValue() { return periodValue; }
        private String beginPeriod() { return beginPeriod; }
        private String endPeriod() { return endPeriod; }
        private String documentDate() { return documentDate; }
        private String description() { return description; }
    }

    private record CompanyProjectMatch(
        String companyProjectCode,
        String companyProjectName,
        String candidate
    ) {
    }
}
