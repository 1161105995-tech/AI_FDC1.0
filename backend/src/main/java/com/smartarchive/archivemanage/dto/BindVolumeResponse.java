package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindVolumeResponse {
    private Long volumeId;
    private String bindVolumeCode;
    private String volumeTitle;
    private String bindRuleKey;
    private String carrierTypeCode;
    private Integer archiveCount;
    private Integer totalPageCount;
    private Integer totalCopyCount;
    private String bindStatus;
    private String remark;
    private List<BindVolumeItemResponse> items;
}
