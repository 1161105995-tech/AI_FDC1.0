package com.smartarchive.archivemanage.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveAiParseResult {
    private String suggestedDocumentTypeCode;
    private String suggestedCarrierTypeCode;
    private String documentName;
    private String businessCode;
    private String sourceSystem;
    private String aiSummary;
    private String extractedTextPreview;
    private Double confidence;
    private Map<String, String> extendedValues;
}
