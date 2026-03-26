package com.smartarchive.documentorganization.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.companyproject.dto.CountryResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationCityResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationCreateCommand;
import com.smartarchive.documentorganization.dto.DocumentOrganizationDetailResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationPermissionPreviewResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationSummaryResponse;
import com.smartarchive.documentorganization.dto.DocumentOrganizationUpdateCommand;
import com.smartarchive.documentorganization.service.DocumentOrganizationService;
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
@RequestMapping("/api/base-data/document-organizations")
@RequiredArgsConstructor
public class DocumentOrganizationController {
    private final DocumentOrganizationService documentOrganizationService;

    @GetMapping
    public ApiResponse<List<DocumentOrganizationSummaryResponse>> list(@RequestParam(required = false) String keyword,
                                                                      @RequestParam(required = false) String documentOrganizationName,
                                                                      @RequestParam(required = false) String countryCode,
                                                                      @RequestParam(required = false) String cityCode,
                                                                      @RequestParam(required = false) String enabledFlag) {
        return ApiResponse.success(documentOrganizationService.list(keyword, documentOrganizationName, countryCode, cityCode, enabledFlag));
    }

    @GetMapping("/countries")
    public ApiResponse<List<CountryResponse>> listCountries() {
        return ApiResponse.success(documentOrganizationService.listCountries());
    }

    @GetMapping("/cities")
    public ApiResponse<List<DocumentOrganizationCityResponse>> listCities(@RequestParam(required = false) String countryCode) {
        return ApiResponse.success(documentOrganizationService.listCities(countryCode));
    }

    @GetMapping("/permissions/preview")
    public ApiResponse<DocumentOrganizationPermissionPreviewResponse> permissionPreview() {
        return ApiResponse.success(documentOrganizationService.getPermissionPreview());
    }

    @GetMapping("/{documentOrganizationCode}")
    public ApiResponse<DocumentOrganizationDetailResponse> detail(@PathVariable String documentOrganizationCode) {
        return ApiResponse.success(documentOrganizationService.getDetail(documentOrganizationCode));
    }

    @PostMapping
    public ApiResponse<DocumentOrganizationDetailResponse> create(@Valid @RequestBody DocumentOrganizationCreateCommand command) {
        return ApiResponse.success(documentOrganizationService.create(command));
    }

    @PutMapping("/{documentOrganizationCode}")
    public ApiResponse<DocumentOrganizationDetailResponse> update(@PathVariable String documentOrganizationCode,
                                                                  @Valid @RequestBody DocumentOrganizationUpdateCommand command) {
        return ApiResponse.success(documentOrganizationService.update(documentOrganizationCode, command));
    }

    @DeleteMapping("/{documentOrganizationCode}")
    public ApiResponse<Void> delete(@PathVariable String documentOrganizationCode) {
        documentOrganizationService.delete(documentOrganizationCode);
        return ApiResponse.success(null);
    }
}
