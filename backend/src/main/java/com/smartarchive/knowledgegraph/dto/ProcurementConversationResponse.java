package com.smartarchive.knowledgegraph.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcurementConversationResponse {
    private Long conversationId;
    private String title;
    private String anchorType;
    private String anchorKey;
    private String scopeMode;
    private String contextSummary;
    private String lastQuestion;
    private String lastAnswer;
    private LocalDateTime lastUpdateDate;
}
