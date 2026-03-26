package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveCreateOptionsResponse {
    private List<LabelValueOption> companyProjects;
    private List<LabelValueOption> documentTypes;
    private List<LabelValueOption> archiveDestinations;
    private List<LabelValueOption> documentOrganizations;
    private List<LabelValueOption> securityLevels;
    private List<LabelValueOption> carrierTypes;
    private List<LabelValueOption> attachmentTypes;
    private List<LabelValueOption> archiveTypes;
    private List<LabelValueOption> aiModels;
}
