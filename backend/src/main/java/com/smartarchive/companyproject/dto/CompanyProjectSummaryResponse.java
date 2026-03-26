package com.smartarchive.companyproject.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CompanyProjectSummaryResponse {
    private Long id;
    private String companyProjectCode;
    private String companyProjectName;
    private String countryCode;
    private String managementArea;
    private String enabledFlag;
    private Integer organizationCount;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
