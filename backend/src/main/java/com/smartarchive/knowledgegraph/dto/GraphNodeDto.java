package com.smartarchive.knowledgegraph.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphNodeDto {
    private String nodeKey;
    private String nodeType;
    private String entityKey;
    private String entityName;
    private String nodeStatus;
    private Long anchorArchiveId;
    private Map<String, Object> attributes;
}
