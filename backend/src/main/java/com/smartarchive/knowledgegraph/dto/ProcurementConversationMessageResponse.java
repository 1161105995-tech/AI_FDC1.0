package com.smartarchive.knowledgegraph.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcurementConversationMessageResponse {
    private Long messageId;
    private Long conversationId;
    private String question;
    private String answer;
    private String contextSummary;
    private ProcurementChainResponse chain;
    private List<String> suggestedFollowUps;
    private LocalDateTime creationDate;
}
