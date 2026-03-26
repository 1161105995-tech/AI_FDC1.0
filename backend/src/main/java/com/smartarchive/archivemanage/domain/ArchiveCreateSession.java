package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_create_session")
public class ArchiveCreateSession {
    @TableId(type = IdType.AUTO)
    private Long sessionId;
    private String sessionCode;
    private String createMode;
    private String sessionStatus;
    private String documentTypeCodeGuess;
    private String carrierTypeCodeGuess;
    private String parseStatus;
    private String aiSummarySnapshot;
    private Long ownerUserId;
    private LocalDateTime expireTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
