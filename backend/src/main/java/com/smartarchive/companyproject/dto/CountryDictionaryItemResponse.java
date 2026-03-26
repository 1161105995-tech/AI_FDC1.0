package com.smartarchive.companyproject.dto;

import lombok.Data;

@Data
public class CountryDictionaryItemResponse {
    private Long id;
    private String countryCode;
    private String countryName;
    private Integer sortOrder;
    private String enabledFlag;
}