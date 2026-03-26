package com.smartarchive.documentorganization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentOrganizationCityResponse {
    private String countryCode;
    private String cityCode;
    private String cityName;
}
