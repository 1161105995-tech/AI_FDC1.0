package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentTypeExtFieldUpdateCommand {
    @NotBlank
    private String fieldName;
    @NotBlank
    private String fieldType;
    private String dictCategoryCode;
    private String requiredFlag = "N";
    private String enabledFlag = "Y";
    private Integer formSortOrder = 1;
    private String queryEnabledFlag = "N";
    private Integer querySortOrder = 1;
}
