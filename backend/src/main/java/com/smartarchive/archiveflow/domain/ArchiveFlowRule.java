package com.smartarchive.archiveflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("md_archive_flow_rule")
public class ArchiveFlowRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyProjectCode;
    private String documentTypeCode;
    private String customRule;
    private String archiveDestination;
    private String documentOrganizationCode;
    private Integer retentionPeriodYears;
    private String securityLevelCode;
    private String externalDisplayFlag;
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
