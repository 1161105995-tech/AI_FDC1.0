package com.smartarchive.companyproject.service;

import com.smartarchive.companyproject.dto.CompanyProjectCreateCommand;
import com.smartarchive.companyproject.dto.CompanyProjectDetailResponse;
import com.smartarchive.companyproject.dto.CompanyProjectOrgCategoryResponse;
import com.smartarchive.companyproject.dto.CompanyProjectPermissionPreviewResponse;
import com.smartarchive.companyproject.dto.CompanyProjectSummaryResponse;
import com.smartarchive.companyproject.dto.CompanyProjectUpdateCommand;
import com.smartarchive.companyproject.dto.CountryDictionaryCommand;
import com.smartarchive.companyproject.dto.CountryDictionaryItemResponse;
import com.smartarchive.companyproject.dto.CountryResponse;
import com.smartarchive.companyproject.dto.OrgCategoryDictionaryCommand;
import com.smartarchive.companyproject.dto.OrgCategoryDictionaryItemResponse;
import java.util.List;

public interface CompanyProjectService {
    List<CompanyProjectSummaryResponse> list(String keyword, String countryCode, String managementArea, String enabledFlag);

    CompanyProjectDetailResponse getDetail(String companyProjectCode);

    List<CountryResponse> listCountries();

    List<CountryDictionaryItemResponse> listCountryDictionaries();

    CountryDictionaryItemResponse createCountryDictionary(CountryDictionaryCommand command);

    CountryDictionaryItemResponse updateCountryDictionary(Long id, CountryDictionaryCommand command);

    void deleteCountryDictionary(Long id);

    List<CompanyProjectOrgCategoryResponse> listOrgCategories();

    List<OrgCategoryDictionaryItemResponse> listOrgCategoryDictionaries();

    OrgCategoryDictionaryItemResponse createOrgCategoryDictionary(OrgCategoryDictionaryCommand command);

    OrgCategoryDictionaryItemResponse updateOrgCategoryDictionary(Long id, OrgCategoryDictionaryCommand command);

    void deleteOrgCategoryDictionary(Long id);

    CompanyProjectDetailResponse create(CompanyProjectCreateCommand command);

    CompanyProjectDetailResponse update(String companyProjectCode, CompanyProjectUpdateCommand command);

    void delete(String companyProjectCode);

    CompanyProjectPermissionPreviewResponse getPermissionPreview();
}