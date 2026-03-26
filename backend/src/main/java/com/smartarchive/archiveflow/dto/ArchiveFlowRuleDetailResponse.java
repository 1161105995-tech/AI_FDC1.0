package com.smartarchive.archiveflow.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ArchiveFlowRuleDetailResponse {
    private Long id;
    private String companyProjectCode;
    private String documentTypeCode;
    private String customRule;
    private String archiveDestination;
    private String documentOrganizationCode;
    private Integer retentionPeriodYears;
    private String securityLevelCode;
    private String externalDisplayFlag;
    private String enabledFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
