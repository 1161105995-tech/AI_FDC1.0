package com.smartarchive.archive.dto;

import lombok.Data;

@Data
public class ArchiveObjectQueryRequest {
    private String keyword;
    private String archiveType;
    private String securityLevel;
    private String organizationName;
}
