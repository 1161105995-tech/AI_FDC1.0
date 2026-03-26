package com.smartarchive.archiveflow.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ArchiveFlowRuleSummaryResponse {
    private Long id;
    private String companyProjectCode;
    private String companyProjectName;
    private String documentTypeCode;
    private String documentTypeName;
    private String customRule;
    private String archiveDestination;
    private String archiveDestinationName;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private Integer retentionPeriodYears;
    private String securityLevelCode;
    private String securityLevelName;
    private String externalDisplayFlag;
    private String enabledFlag;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
