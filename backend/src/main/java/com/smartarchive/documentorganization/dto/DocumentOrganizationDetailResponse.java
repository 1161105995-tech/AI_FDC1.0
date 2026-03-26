package com.smartarchive.documentorganization.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DocumentOrganizationDetailResponse {
    private Long id;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private String description;
    private String countryCode;
    private String cityCode;
    private String enabledFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
