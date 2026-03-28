package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindPreviewResponse {
    private String bindMode;
    private Integer groupCount;
    private Integer archiveCount;
    private List<BindVolumeResponse> groups;
}
