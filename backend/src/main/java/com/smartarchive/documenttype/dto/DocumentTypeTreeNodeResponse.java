package com.smartarchive.documenttype.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DocumentTypeTreeNodeResponse {
    private Long id;
    private String typeCode;
    private String typeName;
    private String description;
    private String enabledFlag;
    private String parentCode;
    private Integer levelNum;
    private String ancestorPath;
    private Integer sortOrder;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private List<DocumentTypeTreeNodeResponse> children = new ArrayList<>();
}
