package com.smartarchive.knowledgegraph.dto;

import lombok.Data;

@Data
public class CreateConversationRequest {
    private String title;
    private String anchorType;
    private String anchorKey;
    private String scopeMode = "CURRENT_CHAIN";
}
