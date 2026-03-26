package com.smartarchive.archiveflow.service;

import com.smartarchive.archiveflow.dto.ArchiveFlowRuleCreateCommand;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleDetailResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleOptionResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRulePermissionPreviewResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleSummaryResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleUpdateCommand;
import com.smartarchive.archiveflow.dto.SecurityLevelOptionResponse;
import java.util.List;

public interface ArchiveFlowRuleService {
    List<ArchiveFlowRuleSummaryResponse> list(String keyword,
                                              String companyProjectCode,
                                              String documentTypeCode,
                                              String documentOrganizationCode,
                                              String enabledFlag);

    ArchiveFlowRuleDetailResponse getDetail(String companyProjectCode);

    ArchiveFlowRuleDetailResponse create(ArchiveFlowRuleCreateCommand command);

    ArchiveFlowRuleDetailResponse update(String companyProjectCode, ArchiveFlowRuleUpdateCommand command);

    void delete(String companyProjectCode);

    List<ArchiveFlowRuleOptionResponse> listCompanyProjectOptions();

    List<ArchiveFlowRuleOptionResponse> listDocumentTypeOptions();

    List<ArchiveFlowRuleOptionResponse> listDocumentOrganizationOptions();

    List<ArchiveFlowRuleOptionResponse> listCityOptions();

    List<SecurityLevelOptionResponse> listSecurityLevels();

    ArchiveFlowRulePermissionPreviewResponse getPermissionPreview();
}
