package com.smartarchive.companyproject.dto;

import lombok.Data;

@Data
public class OrgCategoryDictionaryItemResponse {
    private Long id;
    private String categoryCode;
    private String categoryName;
    private Integer sortOrder;
    private String enabledFlag;
}