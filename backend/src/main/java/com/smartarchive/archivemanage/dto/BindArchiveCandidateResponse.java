package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindArchiveCandidateResponse {
    private Long archiveId;
    private String archiveCode;
    private String documentName;
    private String documentTypeCode;
    private String companyProjectCode;
    private String businessCode;
    private String beginPeriod;
    private String endPeriod;
    private String archiveStatus;
    private String carrierTypeCode;
    private String bindVolumeCode;
}
