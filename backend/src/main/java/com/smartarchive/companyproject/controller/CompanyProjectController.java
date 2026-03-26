package com.smartarchive.companyproject.controller;

import com.smartarchive.common.api.ApiResponse;
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
import com.smartarchive.companyproject.service.CompanyProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/company-projects")
@RequiredArgsConstructor
public class CompanyProjectController {
    private final CompanyProjectService companyProjectService;

    @GetMapping
    public ApiResponse<List<CompanyProjectSummaryResponse>> list(@RequestParam(required = false) String keyword,
                                                                 @RequestParam(required = false) String countryCode,
                                                                 @RequestParam(required = false) String managementArea,
                                                                 @RequestParam(required = false) String enabledFlag) {
        return ApiResponse.success(companyProjectService.list(keyword, countryCode, managementArea, enabledFlag));
    }

    @GetMapping("/countries")
    public ApiResponse<List<CountryResponse>> listCountries() {
        return ApiResponse.success(companyProjectService.listCountries());
    }

    @GetMapping("/dictionary/countries")
    public ApiResponse<List<CountryDictionaryItemResponse>> listCountryDictionaries() {
        return ApiResponse.success(companyProjectService.listCountryDictionaries());
    }

    @PostMapping("/dictionary/countries")
    public ApiResponse<CountryDictionaryItemResponse> createCountryDictionary(@Valid @RequestBody CountryDictionaryCommand command) {
        return ApiResponse.success(companyProjectService.createCountryDictionary(command));
    }

    @PutMapping("/dictionary/countries/{id}")
    public ApiResponse<CountryDictionaryItemResponse> updateCountryDictionary(@PathVariable Long id,
                                                                              @Valid @RequestBody CountryDictionaryCommand command) {
        return ApiResponse.success(companyProjectService.updateCountryDictionary(id, command));
    }

    @DeleteMapping("/dictionary/countries/{id}")
    public ApiResponse<Void> deleteCountryDictionary(@PathVariable Long id) {
        companyProjectService.deleteCountryDictionary(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/org-categories")
    public ApiResponse<List<CompanyProjectOrgCategoryResponse>> listOrgCategories() {
        return ApiResponse.success(companyProjectService.listOrgCategories());
    }

    @GetMapping("/dictionary/org-categories")
    public ApiResponse<List<OrgCategoryDictionaryItemResponse>> listOrgCategoryDictionaries() {
        return ApiResponse.success(companyProjectService.listOrgCategoryDictionaries());
    }

    @PostMapping("/dictionary/org-categories")
    public ApiResponse<OrgCategoryDictionaryItemResponse> createOrgCategoryDictionary(@Valid @RequestBody OrgCategoryDictionaryCommand command) {
        return ApiResponse.success(companyProjectService.createOrgCategoryDictionary(command));
    }

    @PutMapping("/dictionary/org-categories/{id}")
    public ApiResponse<OrgCategoryDictionaryItemResponse> updateOrgCategoryDictionary(@PathVariable Long id,
                                                                                      @Valid @RequestBody OrgCategoryDictionaryCommand command) {
        return ApiResponse.success(companyProjectService.updateOrgCategoryDictionary(id, command));
    }

    @DeleteMapping("/dictionary/org-categories/{id}")
    public ApiResponse<Void> deleteOrgCategoryDictionary(@PathVariable Long id) {
        companyProjectService.deleteOrgCategoryDictionary(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/permissions/preview")
    public ApiResponse<CompanyProjectPermissionPreviewResponse> permissionPreview() {
        return ApiResponse.success(companyProjectService.getPermissionPreview());
    }

    @GetMapping("/{companyProjectCode}")
    public ApiResponse<CompanyProjectDetailResponse> detail(@PathVariable String companyProjectCode) {
        return ApiResponse.success(companyProjectService.getDetail(companyProjectCode));
    }

    @PostMapping
    public ApiResponse<CompanyProjectDetailResponse> create(@Valid @RequestBody CompanyProjectCreateCommand command) {
        return ApiResponse.success(companyProjectService.create(command));
    }

    @PutMapping("/{companyProjectCode}")
    public ApiResponse<CompanyProjectDetailResponse> update(@PathVariable String companyProjectCode,
                                                            @Valid @RequestBody CompanyProjectUpdateCommand command) {
        return ApiResponse.success(companyProjectService.update(companyProjectCode, command));
    }

    @DeleteMapping("/{companyProjectCode}")
    public ApiResponse<Void> delete(@PathVariable String companyProjectCode) {
        companyProjectService.delete(companyProjectCode);
        return ApiResponse.success(null);
    }
}