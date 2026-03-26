package com.smartarchive.companyproject.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CompanyProjectLineResponse {
    private Long id;
    private Integer lineNo;
    private String orgCategory;
    private String organizationCode;
    private String organizationName;
    private String validFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
