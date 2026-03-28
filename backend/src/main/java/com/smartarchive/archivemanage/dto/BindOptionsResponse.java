package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindOptionsResponse {
    private List<LabelValueOption> bindModes;
    private List<BindArchiveCandidateResponse> candidates;
}
