package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_receipt")
public class ArchiveReceipt {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String receiptCode;
    private String sourceDept;
    private String archiveTitle;
    private String archiveType;
    private String securityLevel;
    private String receiveStatus;
    private String workflowInstanceCode;
    private String submittedBy;
    private LocalDateTime submittedAt;
}
