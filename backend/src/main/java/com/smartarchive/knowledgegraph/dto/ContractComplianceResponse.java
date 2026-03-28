package com.smartarchive.knowledgegraph.dto;

import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractComplianceResponse {
    private String contractNo;
    private String overallStatus;
    private String summary;
    private List<ComplianceCheckItemDto> checks;
    private List<String> anomalies;
    private List<ArchiveSummaryResponse> references;
}
