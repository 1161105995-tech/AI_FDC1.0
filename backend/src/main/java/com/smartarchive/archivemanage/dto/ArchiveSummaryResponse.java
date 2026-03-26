package com.smartarchive.archivemanage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveSummaryResponse {
    private Long archiveId;
    private String archiveCode;
    private String archiveFilingCode;
    private String documentTypeCode;
    private String documentTypeName;
    private String companyProjectCode;
    private String companyProjectName;
    private String documentName;
    private String businessCode;
    private String dutyPerson;
    private String dutyDepartment;
    private LocalDate documentDate;
    private String securityLevelCode;
    private String sourceSystem;
    private String archiveDestination;
    private String originPlace;
    private String carrierTypeCode;
    private String aiArchiveSummary;
    private String documentOrganizationCode;
    private Integer retentionPeriodYears;
    private String archiveTypeCode;
    private String archiveStatus;
    private String parseStatus;
    private String vectorStatus;
    private LocalDateTime lastUpdateDate;
    private Integer attachmentCount;
    private Map<String, String> extValues;
    private List<ArchiveAttachmentResponse> attachments;
}
