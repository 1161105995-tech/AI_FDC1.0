package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageQueryResponse {
    private List<BindVolumeResponse> volumes;
    private List<BindArchiveCandidateResponse> archives;
}
