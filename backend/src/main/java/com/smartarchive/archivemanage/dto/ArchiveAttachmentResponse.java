package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveAttachmentResponse {
    private Long attachmentId;
    private String attachmentRole;
    private String attachmentTypeCode;
    private Integer attachmentSeq;
    private Integer versionNo;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String remark;
    private String aiSummary;
    private String parseStatus;
    private String vectorStatus;
    private LocalDateTime creationDate;
}
