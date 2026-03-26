package com.smartarchive.documentorganization.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DocumentOrganizationSummaryResponse {
    private Long id;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private String description;
    private String countryCode;
    private String cityCode;
    private String enabledFlag;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
