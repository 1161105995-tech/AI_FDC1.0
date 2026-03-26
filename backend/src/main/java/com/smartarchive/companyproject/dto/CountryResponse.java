package com.smartarchive.companyproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryResponse {
    private String countryCode;
    private String countryName;
}
