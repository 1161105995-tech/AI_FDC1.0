package com.smartarchive.knowledgegraph.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceItemDto {
    private Long archiveId;
    private String archiveCode;
    private String documentName;
    private String sourceFieldCode;
    private String sourceSemanticCode;
    private String edgeType;
    private String summary;
}
