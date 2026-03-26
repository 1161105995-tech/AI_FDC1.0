package com.smartarchive.companyproject.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CompanyProjectDetailResponse {
    private Long id;
    private String companyProjectCode;
    private String companyProjectName;
    private String countryCode;
    private String managementArea;
    private String enabledFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private List<CompanyProjectLineResponse> lines;
}
