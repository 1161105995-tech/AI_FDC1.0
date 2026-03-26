package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_attachment")
public class ArchiveAttachment {
    @TableId(type = IdType.AUTO)
    private Long attachmentId;
    private Long archiveId;
    private Long sessionId;
    private String attachmentRole;
    private String attachmentTypeCode;
    private Integer attachmentSeq;
    private Integer versionNo;
    private String fileName;
    private String fileExt;
    private String mimeType;
    private Long fileSize;
    private String storagePath;
    private String storageKey;
    private String fileHash;
    private String remark;
    private String aiSummary;
    private String ocrStatus;
    private String parseStatus;
    private String vectorStatus;
    private String activeFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
