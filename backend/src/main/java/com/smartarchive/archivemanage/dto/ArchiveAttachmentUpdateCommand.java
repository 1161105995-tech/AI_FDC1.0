package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class ArchiveAttachmentUpdateCommand {
    private String attachmentTypeCode;
    private String remark;
    private String aiSummary;
}
