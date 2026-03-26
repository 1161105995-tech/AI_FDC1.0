package com.smartarchive.archiveflow.controller;

import com.smartarchive.archiveflow.dto.ArchiveFlowRuleCreateCommand;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleDetailResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleOptionResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRulePermissionPreviewResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleSummaryResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleUpdateCommand;
import com.smartarchive.archiveflow.dto.SecurityLevelOptionResponse;
import com.smartarchive.archiveflow.service.ArchiveFlowRuleService;
import com.smartarchive.common.api.ApiResponse;
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
@RequestMapping("/api/base-data/archive-flow-rules")
@RequiredArgsConstructor
public class ArchiveFlowRuleController {
    private final ArchiveFlowRuleService archiveFlowRuleService;

    @GetMapping
    public ApiResponse<List<ArchiveFlowRuleSummaryResponse>> list(@RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) String companyProjectCode,
                                                                  @RequestParam(required = false) String documentTypeCode,
                                                                  @RequestParam(required = false) String documentOrganizationCode,
                                                                  @RequestParam(required = false) String enabledFlag) {
        return ApiResponse.success(archiveFlowRuleService.list(keyword, companyProjectCode, documentTypeCode, documentOrganizationCode, enabledFlag));
    }

    @GetMapping("/options/company-projects")
    public ApiResponse<List<ArchiveFlowRuleOptionResponse>> listCompanyProjects() {
        return ApiResponse.success(archiveFlowRuleService.listCompanyProjectOptions());
    }

    @GetMapping("/options/document-types")
    public ApiResponse<List<ArchiveFlowRuleOptionResponse>> listDocumentTypes() {
        return ApiResponse.success(archiveFlowRuleService.listDocumentTypeOptions());
    }

    @GetMapping("/options/document-organizations")
    public ApiResponse<List<ArchiveFlowRuleOptionResponse>> listDocumentOrganizations() {
        return ApiResponse.success(archiveFlowRuleService.listDocumentOrganizationOptions());
    }

    @GetMapping("/options/cities")
    public ApiResponse<List<ArchiveFlowRuleOptionResponse>> listCities() {
        return ApiResponse.success(archiveFlowRuleService.listCityOptions());
    }

    @GetMapping("/options/security-levels")
    public ApiResponse<List<SecurityLevelOptionResponse>> listSecurityLevels() {
        return ApiResponse.success(archiveFlowRuleService.listSecurityLevels());
    }

    @GetMapping("/permissions/preview")
    public ApiResponse<ArchiveFlowRulePermissionPreviewResponse> permissionPreview() {
        return ApiResponse.success(archiveFlowRuleService.getPermissionPreview());
    }

    @GetMapping("/{companyProjectCode}")
    public ApiResponse<ArchiveFlowRuleDetailResponse> detail(@PathVariable String companyProjectCode) {
        return ApiResponse.success(archiveFlowRuleService.getDetail(companyProjectCode));
    }

    @PostMapping
    public ApiResponse<ArchiveFlowRuleDetailResponse> create(@Valid @RequestBody ArchiveFlowRuleCreateCommand command) {
        return ApiResponse.success(archiveFlowRuleService.create(command));
    }

    @PutMapping("/{companyProjectCode}")
    public ApiResponse<ArchiveFlowRuleDetailResponse> update(@PathVariable String companyProjectCode,
                                                             @Valid @RequestBody ArchiveFlowRuleUpdateCommand command) {
        return ApiResponse.success(archiveFlowRuleService.update(companyProjectCode, command));
    }

    @DeleteMapping("/{companyProjectCode}")
    public ApiResponse<Void> delete(@PathVariable String companyProjectCode) {
        archiveFlowRuleService.delete(companyProjectCode);
        return ApiResponse.success(null);
    }
}
