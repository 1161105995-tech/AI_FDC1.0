package com.smartarchive.knowledgegraph.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

@Data
public class ProcurementAskRequest {
    private Long conversationId;
    @NotBlank
    private String question;
    private String anchorType;
    private String anchorKey;
    private String scopeMode = "CURRENT_CHAIN";
    private Map<String, String> filters;
}
