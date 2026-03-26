package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentTypeExtFieldResponse {
    private Long fieldId;
    private String fieldCode;
    private String documentTypeCode;
    private String fieldName;
    private String fieldType;
    private String dictCategoryCode;
    private String requiredFlag;
    private String enabledFlag;
    private Integer formSortOrder;
    private String queryEnabledFlag;
    private Integer querySortOrder;
    private Integer sourceLevel;
    private String sourceDocumentTypeCode;
    private LocalDateTime lastUpdateDate;
}
