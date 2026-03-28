package com.smartarchive.knowledgegraph.dto;

import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcurementChainResponse {
    private String anchorNodeKey;
    private String contextSummary;
    private List<GraphNodeDto> nodes;
    private List<GraphEdgeDto> edges;
    private List<EvidenceItemDto> evidenceItems;
    private List<TimelineEventDto> timeline;
    private List<String> anomalies;
    private List<ComplianceCheckItemDto> complianceChecks;
    private List<ArchiveSummaryResponse> references;
}
