package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_disposal_record")
public class DisposalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String disposalCode;
    private String archiveCode;
    private String archiveTitle;
    private String retentionPeriod;
    private String appraisalConclusion;
    private String approvalStatus;
    private String disposalStatus;
    private LocalDateTime createdAt;
}