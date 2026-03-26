package com.smartarchive.companyproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyProjectOrgCategoryResponse {
    private String categoryCode;
    private String categoryName;
}
