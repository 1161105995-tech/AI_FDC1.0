package com.smartarchive.archivemanage.dto;

import java.util.Map;
import lombok.Data;

@Data
public class ArchiveQueryCommand {
    private String keyword;
    private String documentTypeCode;
    private String companyProjectCode;
    private String archiveTypeCode;
    private String carrierTypeCode;
    private String securityLevelCode;
    private String beginPeriod;
    private String endPeriod;
    private String documentName;
    private String businessCode;
    private String dutyPerson;
    private String archiveDestination;
    private String sourceSystem;
    private String documentOrganizationCode;
    private Map<String, String> extFilters;
}
