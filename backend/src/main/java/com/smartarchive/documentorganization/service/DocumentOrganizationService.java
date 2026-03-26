package com.smartarchive.documentorganization.service;

import com.smartarchive.companyproject.dto.CountryResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationCityResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationCreateCommand;
import com.smartarchive.documentorganization.dto.DocumentOrganizationDetailResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationPermissionPreviewResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationSummaryResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationUpdateCommand;
import java.util.List;

public interface DocumentOrganizationService {
    List<DocumentOrganizationSummaryResponse> list(String keyword, String documentOrganizationName, String countryCode, String cityCode, String enabledFlag);

    DocumentOrganizationDetailResponse getDetail(String documentOrganizationCode);

    List<CountryResponse> listCountries();

    List<DocumentOrganizationCityResponse> listCities(String countryCode);

    DocumentOrganizationDetailResponse create(DocumentOrganizationCreateCommand command);

    DocumentOrganizationDetailResponse update(String documentOrganizationCode, DocumentOrganizationUpdateCommand command);

    void delete(String documentOrganizationCode);

    DocumentOrganizationPermissionPreviewResponse getPermissionPreview();
}
