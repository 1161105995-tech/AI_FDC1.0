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
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.LabelValueOption;
import com.smartarchive.archivemanage.mapper.AiModelConfigMapper;
import com.smartarchive.archivemanage.mapper.ArchiveAttachmentMapper;
import com.smartarchive.archivemanage.mapper.ArchiveContentChunkMapper;
import com.smartarchive.archivemanage.mapper.ArchiveContentMapper;
import com.smartarchive.archivemanage.mapper.ArchiveCreateSessionMapper;
import com.smartarchive.archivemanage.mapper.ArchiveExtValueMapper;
import com.smartarchive.archivemanage.mapper.ArchivePaperMapper;
import com.smartarchive.archivemanage.mapper.ArchiveRecordMapper;
import com.smartarchive.archivemanage.service.ArchiveManagementService;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.archivemanage.service.support.ArchiveAiChatService;
import com.smartarchive.archivemanage.service.support.ArchiveFileTextExtractor;
import com.smartarchive.archivemanage.service.support.ArchiveTextChunkService;
import com.smartarchive.archivemanage.service.support.ArchiveTextVectorService;
import com.smartarchive.archiveflow.domain.ArchiveFlowRule;
import com.smartarchive.archiveflow.domain.SecurityLevelDictionary;
import com.smartarchive.archiveflow.mapper.ArchiveFlowRuleMapper;
import com.smartarchive.archiveflow.mapper.SecurityLevelDictionaryMapper;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyproject.domain.CompanyProject;
import com.smartarchive.companyproject.mapper.CompanyProjectMapper;
import com.smartarchive.documentorganization.domain.DocumentOrganization;
import com.smartarchive.documentorganization.domain.DocumentOrganizationCity;
import com.smartarchive.documentorganization.mapper.DocumentOrganizationCityMapper;
import com.smartarchive.documentorganization.mapper.DocumentOrganizationMapper;
import com.smartarchive.documenttype.domain.DocumentType;
import com.smartarchive.documenttype.mapper.DocumentTypeMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArchiveManagementServiceImpl implements ArchiveManagementService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final Pattern BUSINESS_CODE_PATTERN = Pattern.compile("[A-Z]{2,}[A-Z0-9_-]{2,}");
    private static final Set<String> TEXT_EXTENSIONS = Set.of("txt", "md", "json", "csv", "xml", "sql", "log", "yml", "yaml", "properties", "java", "ts", "js", "vue", "html", "htm");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\\d{4}-\\d{2}");
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("姓名[：:：]?\\s*([\\p{IsHan}A-Za-z·]{2,20})");

    private final ArchiveRecordMapper archiveRecordMapper;
    private final ArchiveExtValueMapper archiveExtValueMapper;
    private final ArchiveCreateSessionMapper archiveCreateSessionMapper;
    private final ArchiveAttachmentMapper archiveAttachmentMapper;
    private final ArchivePaperMapper archivePaperMapper;
    private final ArchiveContentMapper archiveContentMapper;
    private final ArchiveContentChunkMapper archiveContentChunkMapper;
    private final AiModelConfigMapper aiModelConfigMapper;
    private final DocumentTypeMapper documentTypeMapper;
    private final CompanyProjectMapper companyProjectMapper;
    private final DocumentOrganizationMapper documentOrganizationMapper;
    private final DocumentOrganizationCityMapper documentOrganizationCityMapper;
    private final ArchiveFlowRuleMapper archiveFlowRuleMapper;
    private final SecurityLevelDictionaryMapper securityLevelDictionaryMapper;
    private final DocumentTypeExtFieldService documentTypeExtFieldService;
    private final JdbcTemplate jdbcTemplate;
    private final ArchiveAiChatService archiveAiChatService;
    private final ArchiveFileTextExtractor archiveFileTextExtractor;
    private final ArchiveTextChunkService archiveTextChunkService;
    private final ArchiveTextVectorService archiveTextVectorService;

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
        List<ArchiveRecord> records = archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
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
            .eq(StringUtils.hasText(command.getDocumentOrganizationCode()), ArchiveRecord::getDocumentOrganizationCode, trimToNull(command.getDocumentOrganizationCode()))
            .orderByDesc(ArchiveRecord::getLastUpdateDate));
        if (StringUtils.hasText(command.getKeyword())) {
            String keyword = command.getKeyword().trim();
            Set<Long> contentMatchedIds = new LinkedHashSet<>(jdbcTemplate.query("select distinct archive_id from arc_archive_content where delete_flag = 'N' and full_text ilike ?", (rs, rowNum) -> rs.getLong(1), "%" + keyword + "%"));
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            records = records.stream().filter(item -> contentMatchedIds.contains(item.getArchiveId()) || containsIgnoreCase(item.getDocumentName(), lowerKeyword) || containsIgnoreCase(item.getBusinessCode(), lowerKeyword) || containsIgnoreCase(item.getArchiveFilingCode(), lowerKeyword) || containsIgnoreCase(item.getAiArchiveSummary(), lowerKeyword)).toList();
        }
        List<Long> archiveIds = records.stream().map(ArchiveRecord::getArchiveId).toList();
        Map<Long, Map<String, String>> extValueMap = loadExtValueMap(archiveIds);
        Map<String, String> extFilters = command.getExtFilters();
        if (extFilters != null && !extFilters.isEmpty()) {
            Map<Long, Map<String, String>> currentExtValueMap = extValueMap;
            records = records.stream().filter(item -> matchesExtFilters(currentExtValueMap.getOrDefault(item.getArchiveId(), Map.of()), extFilters)).toList();
            archiveIds = records.stream().map(ArchiveRecord::getArchiveId).toList();
            extValueMap = loadExtValueMap(archiveIds);
        }
        Map<Long, List<ArchiveAttachment>> attachmentMap = loadAttachmentMap(archiveIds);
        Map<Long, Map<String, String>> finalExtValueMap = extValueMap;
        Map<Long, List<ArchiveAttachment>> finalAttachmentMap = attachmentMap;
        List<DocumentTypeExtFieldResponse> queryFields = StringUtils.hasText(command.getDocumentTypeCode()) ? documentTypeExtFieldService.listEffective(command.getDocumentTypeCode()).stream().filter(item -> "Y".equals(item.getQueryEnabledFlag())).toList() : List.of();
        return ArchiveQueryResponse.builder().records(records.stream().map(item -> buildArchiveSummary(item, finalExtValueMap.getOrDefault(item.getArchiveId(), Map.of()), finalAttachmentMap.getOrDefault(item.getArchiveId(), List.of()))).toList()).queryFields(queryFields).build();
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

    private List<LabelValueOption> listEnabledCompanyProjects() {
        return companyProjectMapper.selectList(new LambdaQueryWrapper<CompanyProject>().eq(CompanyProject::getDeleteFlag, "N").eq(CompanyProject::getEnabledFlag, "Y").orderByAsc(CompanyProject::getCompanyProjectCode)).stream().map(item -> option(item.getCompanyProjectCode(), item.getCompanyProjectName())).toList();
    }

    private List<LabelValueOption> listEnabledDocumentTypes() {
        return documentTypeMapper.selectList(new LambdaQueryWrapper<DocumentType>().eq(DocumentType::getDeleteFlag, "N").eq(DocumentType::getEnabledFlag, "Y").orderByAsc(DocumentType::getLevelNum).orderByAsc(DocumentType::getSortOrder).orderByAsc(DocumentType::getTypeCode)).stream().map(item -> option(item.getTypeCode(), item.getTypeName())).toList();
    }

    private List<LabelValueOption> listEnabledDocumentOrganizations() {
        return jdbcTemplate.query("select document_organization_code from md_document_organization where enabled_flag = 'Y' order by document_organization_code", (rs, rowNum) -> option(rs.getString(1), rs.getString(1)));
    }

    private List<LabelValueOption> listEnabledCities() {
        return documentOrganizationCityMapper.selectList(new LambdaQueryWrapper<DocumentOrganizationCity>().eq(DocumentOrganizationCity::getEnabledFlag, "Y").in(DocumentOrganizationCity::getDeleteFlag, List.of("N", "Y")).orderByAsc(DocumentOrganizationCity::getCountryCode).orderByAsc(DocumentOrganizationCity::getSortOrder)).stream().map(item -> option(item.getCityCode(), item.getCityName())).toList();
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

    private Map<Long, Map<String, String>> loadExtValueMap(List<Long> archiveIds) {
        if (archiveIds == null || archiveIds.isEmpty()) return Map.of();
        return archiveExtValueMapper.selectList(new LambdaQueryWrapper<ArchiveExtValue>().in(ArchiveExtValue::getArchiveId, archiveIds)).stream().collect(Collectors.groupingBy(ArchiveExtValue::getArchiveId, LinkedHashMap::new, Collectors.toMap(ArchiveExtValue::getFieldCode, this::resolveExtValue, (left, right) -> right, LinkedHashMap::new)));
    }

    private boolean matchesExtFilters(Map<String, String> extValues, Map<String, String> filters) {
        return filters.entrySet().stream().filter(entry -> StringUtils.hasText(entry.getValue())).allMatch(entry -> containsIgnoreCase(extValues.get(entry.getKey()), entry.getValue().trim().toLowerCase(Locale.ROOT)));
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
            MetadataFallback fallback = inferMetadataFallback(originalFilename, text);
            String effectiveText = StringUtils.hasText(text) ? text : buildFallbackText(originalFilename, fallback);
            return new ParsedAttachment(
                effectiveText,
                summarizeText(originalFilename, effectiveText),
                guessDocumentTypeCode(originalFilename, effectiveText, fallback.docType()),
                guessBusinessCode(effectiveText),
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
            .sourceSystem("AI_UPLOAD")
            .aiSummary(parsed.summary())
            .extractedTextPreview(parsed.preview())
            .confidence(parsed.confidence())
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

    private MetadataFallback inferMetadataFallback(String fileName, String fullText) {
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
        return matcher.find() ? matcher.group() : null;
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

    private record ParsedAttachment(String fullText, String summary, String suggestedDocumentTypeCode, String businessCode, Double confidence, Map<String, String> extendedValues, boolean ocrEnhanced) {
        boolean hasText() { return StringUtils.hasText(fullText); }
        String preview() { if (!StringUtils.hasText(fullText)) return ""; String normalized = fullText.replaceAll("\\s+", " ").trim(); return normalized.length() > 200 ? normalized.substring(0, 200) + "..." : normalized; }
    }

    private record SemanticMatchRow(Long archiveId, String chunkText, Double distance) {
    }

    private record MetadataFallback(
        String docSource,
        String docType,
        String companyName,
        String departmentName,
        String projectName,
        String periodValue,
        String description
    ) {
    }
}
