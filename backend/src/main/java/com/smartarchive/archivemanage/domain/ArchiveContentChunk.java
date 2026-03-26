package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_content_chunk")
public class ArchiveContentChunk {
    @TableId(type = IdType.AUTO)
    private Long chunkId;
    private Long contentId;
    private Long archiveId;
    private Long attachmentId;
    private Integer chunkNo;
    private String chunkText;
    private Integer tokenCount;
    private Integer pageNo;
    private Integer positionStart;
    private Integer positionEnd;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
