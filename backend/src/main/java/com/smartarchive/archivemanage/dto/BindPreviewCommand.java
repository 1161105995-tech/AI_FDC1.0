package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Data;

@Data
public class BindPreviewCommand {
    private String bindMode;
    private String keyword;
    private String documentTypeCode;
    private String companyProjectCode;
    private List<Long> archiveIds;
}
