package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveQueryResponse {
    private List<ArchiveSummaryResponse> records;
    private List<DocumentTypeExtFieldResponse> queryFields;
    private Long total;
    private Integer page;
    private Integer pageSize;
}
