package com.smartarchive.knowledgegraph.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierAccountTimelineResponse {
    private String supplierName;
    private boolean changedInLastThreeYears;
    private List<String> accountNumbers;
    private List<TimelineEventDto> timeline;
    private List<EvidenceItemDto> evidenceItems;
}
