package com.smartarchive.knowledgegraph.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcurementAskResponse {
    private Long conversationId;
    private String answer;
    private String contextSummary;
    private ProcurementChainResponse chain;
    private java.util.List<String> suggestedFollowUps;
}
