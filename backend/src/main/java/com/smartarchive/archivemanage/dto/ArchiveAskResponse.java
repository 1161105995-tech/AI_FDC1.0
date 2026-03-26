package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveAskResponse {
    private String answer;
    private List<ArchiveSummaryResponse> references;
}
