package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class ArchiveDefaultResolveResponse {
    private String securityLevelCode;
    private String archiveDestination;
    private String documentOrganizationCode;
    private Integer retentionPeriodYears;
    private String countryCode;
}
